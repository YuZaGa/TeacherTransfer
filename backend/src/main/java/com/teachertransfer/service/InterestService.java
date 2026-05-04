package com.teachertransfer.service;

import com.teachertransfer.dto.interest.InterestResponse;
import com.teachertransfer.entity.Block;
import com.teachertransfer.entity.Notification;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TransferInterest;
import com.teachertransfer.enums.InterestStatus;
import com.teachertransfer.enums.InterestType;
import com.teachertransfer.repository.BlockRepository;
import com.teachertransfer.repository.MatchResultRepository;
import com.teachertransfer.repository.NotificationRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.repository.TransferInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterestService {

    private static final Logger log = LoggerFactory.getLogger(InterestService.class);

    @Autowired
    private TransferInterestRepository interestRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private BlockRepository blockRepository;

    public InterestResponse sendInterest(Long fromTeacherId, Long toTeacherId) {
        Teacher fromTeacher = teacherRepository.findById(fromTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Teacher toTeacher = teacherRepository.findById(toTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TransferInterest interest;
        var existing = interestRepository.findExistingInterest(fromTeacherId, toTeacherId);
        if (existing.isPresent()) {
            interest = existing.get();
            if (interest.getStatus() == InterestStatus.PENDING.getCode() ||
                interest.getStatus() == InterestStatus.ACCEPTED.getCode()) {
                throw new RuntimeException("Interest already sent");
            }
            interest.setStatus(InterestStatus.PENDING.getCode());
            interest.setType(InterestType.ONE_WAY.getCode());
            interest.setCreatedAt(LocalDateTime.now());
            interest.setRespondedAt(null);
        } else {
            interest = new TransferInterest();
            interest.setFromTeacherId(fromTeacherId);
            interest.setToTeacherId(toTeacherId);
            interest.setType(InterestType.ONE_WAY.getCode());
            interest.setStatus(InterestStatus.PENDING.getCode());
            interest.setCreatedAt(LocalDateTime.now());
        }

        interest = interestRepository.save(interest);

        fromTeacher.setLastInteractionAt(LocalDateTime.now());
        fromTeacher.setProfileUpdatedAt(LocalDateTime.now());
        teacherRepository.save(fromTeacher);

        boolean isMutual = interestRepository.findExistingInterest(toTeacherId, fromTeacherId)
                .filter(i -> i.getStatus().equals(InterestStatus.PENDING.getCode()) ||
                        i.getStatus().equals(InterestStatus.ACCEPTED.getCode()))
                .isPresent();

        if (isMutual) {
            interest.setType(InterestType.MUTUAL.getCode());
            interest.setStatus(InterestStatus.ACCEPTED.getCode());
            interest.setRespondedAt(LocalDateTime.now());
            interest = interestRepository.save(interest);

            TransferInterest reverse = interestRepository.findExistingInterest(toTeacherId, fromTeacherId).get();
            reverse.setType(InterestType.MUTUAL.getCode());
            reverse.setStatus(InterestStatus.ACCEPTED.getCode());
            reverse.setRespondedAt(LocalDateTime.now());
            interestRepository.save(reverse);
        }

        Notification notification = new Notification();
        notification.setTeacherId(toTeacherId);
        notification.setTitle(isMutual ? "Mutual Match Found!" : "New Transfer Interest");
        notification.setMessage(isMutual
                ? "You and " + fromTeacher.getName() + " have a mutual match!"
                : "A teacher is interested in transferring with you");
        notification.setType(isMutual ? "MUTUAL_MATCH" : "INTEREST_RECEIVED");
        notification.setRelatedTeacherId(fromTeacherId);
        notification.setRelatedInterestId(interest.getId());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        if (isMutual) {
            Notification mutualNotification = new Notification();
            mutualNotification.setTeacherId(fromTeacherId);
            mutualNotification.setTitle("Mutual Match Found!");
            mutualNotification.setMessage("You and " + toTeacher.getName() + " have a mutual match!");
            mutualNotification.setType("MUTUAL_MATCH");
            mutualNotification.setRelatedTeacherId(toTeacherId);
            mutualNotification.setRelatedInterestId(interest.getId());
            mutualNotification.setRead(false);
            mutualNotification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(mutualNotification);
        }

        return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual, true);
    }

    public InterestResponse acceptInterest(Long interestId, Long teacherId) {
        TransferInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        if (!interest.getToTeacherId().equals(teacherId)) {
            throw new RuntimeException("You can only accept interests sent to you");
        }

        if (interest.getStatus() != InterestStatus.PENDING.getCode()) {
            throw new RuntimeException("Interest already responded");
        }

        interest.setStatus(InterestStatus.ACCEPTED.getCode());
        interest.setRespondedAt(LocalDateTime.now());
        interest.setType(InterestType.MUTUAL.getCode());
        interest = interestRepository.save(interest);

        Teacher acceptor = teacherRepository.findById(teacherId).orElse(null);
        if (acceptor != null) {
            acceptor.setLastInteractionAt(LocalDateTime.now());
            acceptor.setProfileUpdatedAt(LocalDateTime.now());
            teacherRepository.save(acceptor);
        }

        Notification notification = new Notification();
        notification.setTeacherId(interest.getFromTeacherId());
        notification.setTitle("Interest Accepted - Mutual Match!");
        notification.setMessage("Your transfer interest has been accepted! Contact details are now unlocked.");
        notification.setType("INTEREST_ACCEPTED");
        notification.setRelatedTeacherId(interest.getToTeacherId());
        notification.setRelatedInterestId(interestId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
        Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);

        return mapToInterestResponse(interest, fromTeacher, toTeacher, true, false);
    }

    public InterestResponse rejectInterest(Long interestId, Long teacherId) {
        TransferInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        if (!interest.getToTeacherId().equals(teacherId)) {
            throw new RuntimeException("You can only reject interests sent to you");
        }

        if (interest.getStatus() != InterestStatus.PENDING.getCode()) {
            throw new RuntimeException("Interest already responded");
        }

        interest.setStatus(InterestStatus.REJECTED.getCode());
        interest.setRespondedAt(LocalDateTime.now());
        interest = interestRepository.save(interest);

        try {
            matchResultRepository.deleteByTeacherIdAndMatchedTeacherId(teacherId, interest.getFromTeacherId());
            matchResultRepository.deleteByTeacherIdAndMatchedTeacherId(interest.getFromTeacherId(), teacherId);
        } catch (Exception e) {
            log.warn("Failed to cleanup match results on reject: {}", e.getMessage());
        }

        teacherService.touchInteraction(teacherId);

        Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
        Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);

        return mapToInterestResponse(interest, fromTeacher, toTeacher, false, false);
    }

    @Transactional
    public void clearAllInteractions(Long teacherId) {
        List<TransferInterest> asSender = interestRepository.findByFromTeacherIdOrderByCreatedAtDesc(teacherId);
        for (TransferInterest i : asSender) interestRepository.delete(i);
        List<TransferInterest> asRecipient = interestRepository.findByToTeacherIdOrderByCreatedAtDesc(teacherId);
        for (TransferInterest i : asRecipient) interestRepository.delete(i);
        matchResultRepository.deleteByTeacherId(teacherId);
        matchResultRepository.deleteByMatchedTeacherId(teacherId);
        notificationRepository.deleteByTeacherId(teacherId);
    }

    public void withdrawInterest(Long interestId, Long teacherId) {
        TransferInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        if (!interest.getFromTeacherId().equals(teacherId)) {
            throw new RuntimeException("You can only withdraw interests you sent");
        }

        if (interest.getTypeEnum() == InterestType.MUTUAL) {
            throw new RuntimeException("Cannot withdraw a mutual match");
        }

        interest.setStatus(InterestStatus.WITHDRAWN.getCode());
        interest.setRespondedAt(LocalDateTime.now());
        interestRepository.save(interest);

        try {
            matchResultRepository.deleteByTeacherIdAndMatchedTeacherId(teacherId, interest.getToTeacherId());
            matchResultRepository.deleteByTeacherIdAndMatchedTeacherId(interest.getToTeacherId(), teacherId);
        } catch (Exception e) {
            log.warn("Failed to cleanup match results on withdraw: {}", e.getMessage());
        }
    }

    public List<InterestResponse> getSentInterests(Long teacherId) {
        List<TransferInterest> interests = interestRepository.findByFromTeacherIdOrderByCreatedAtDesc(teacherId);
        return interests.stream()
                .map(interest -> {
                    Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
                    Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
                    boolean isMutual = interest.getStatus().equals(InterestStatus.ACCEPTED.getCode());
                    return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual, true);
                })
                .collect(Collectors.toList());
    }

    public List<InterestResponse> getReceivedInterests(Long teacherId) {
        List<TransferInterest> interests = interestRepository.findByToTeacherIdOrderByCreatedAtDesc(teacherId);
        return interests.stream()
                .map(interest -> {
                    Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
                    Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
                    boolean isMutual = interest.getStatus().equals(InterestStatus.ACCEPTED.getCode());
                    return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual, false);
                })
                .collect(Collectors.toList());
    }

    private InterestResponse mapToInterestResponse(TransferInterest interest, Teacher fromTeacher, Teacher toTeacher, boolean isMutual, boolean viewerIsSender) {
        InterestResponse response = new InterestResponse();
        response.setId(interest.getId());
        response.setFromTeacherId(interest.getFromTeacherId());
        response.setToTeacherId(interest.getToTeacherId());
        response.setType(InterestType.fromCode(interest.getType()));
        response.setStatus(InterestStatus.fromCode(interest.getStatus()));
        response.setCreatedAt(interest.getCreatedAt());
        response.setRespondedAt(interest.getRespondedAt());

        if (fromTeacher != null) {
            response.setFromTeacherName(fromTeacher.getName());
            response.setFromTeacherBlockName(resolveBlockName(fromTeacher));
            if (isMutual) {
                response.setFromTeacherPhone(fromTeacher.getPhone());
                response.setFromTeacherSchool(fromTeacher.getSchoolName());
            }
        }

        if (toTeacher != null) {
            response.setToTeacherName(toTeacher.getName());
            response.setToTeacherBlockName(resolveBlockName(toTeacher));
            if (isMutual) {
                response.setToTeacherPhone(toTeacher.getPhone());
                response.setToTeacherSchool(toTeacher.getSchoolName());
            }
        }

        if (fromTeacher != null && toTeacher != null) {
            if (viewerIsSender) {
                response.setDistanceKm(haversineDistance(
                    fromTeacher.getPreferredLat(), fromTeacher.getPreferredLng(),
                    toTeacher.getCurrentLat(), toTeacher.getCurrentLng()
                ));
            } else {
                response.setDistanceKm(haversineDistance(
                    toTeacher.getPreferredLat(), toTeacher.getPreferredLng(),
                    fromTeacher.getCurrentLat(), fromTeacher.getCurrentLng()
                ));
            }
        }

        return response;
    }

    private String resolveBlockName(Teacher teacher) {
        if (teacher.getCurrentBlockId() == null) return "Unknown Block";
        return blockRepository.findById(teacher.getCurrentBlockId())
                .map(Block::getName)
                .orElse("Block " + teacher.getCurrentBlockId());
    }

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        if (lat2 == 0 && lng2 == 0) return 0;
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 10.0) / 10.0;
    }
}
