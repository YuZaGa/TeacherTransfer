package com.teachertransfer.service;

import com.teachertransfer.dto.interest.InterestResponse;
import com.teachertransfer.entity.Notification;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TransferInterest;
import com.teachertransfer.enums.InterestStatus;
import com.teachertransfer.enums.InterestType;
import com.teachertransfer.repository.NotificationRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.repository.TransferInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterestService {

    @Autowired
    private TransferInterestRepository interestRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeacherService teacherService;

    public InterestResponse sendInterest(Long fromTeacherId, Long toTeacherId) {
        Teacher fromTeacher = teacherRepository.findById(fromTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Teacher toTeacher = teacherRepository.findById(toTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        var existing = interestRepository.findExistingInterest(fromTeacherId, toTeacherId);
        TransferInterest interest;
        if (existing.isPresent()) {
            interest = existing.get();
            if (!Boolean.TRUE.equals(interest.getIsOutdated())) {
                throw new RuntimeException("Interest already sent");
            }
            interest.setIsOutdated(false);
            interest.setStatus(InterestStatus.PENDING.getCode());
            interest.setUpdatedAt(LocalDateTime.now());
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
            interest = interestRepository.save(interest);

            TransferInterest reverse = interestRepository.findExistingInterest(toTeacherId, fromTeacherId).get();
            reverse.setType(InterestType.MUTUAL.getCode());
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

        return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual);
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

        return mapToInterestResponse(interest, fromTeacher, toTeacher, true);
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

        teacherService.touchInteraction(teacherId);

        Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
        Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);

        return mapToInterestResponse(interest, fromTeacher, toTeacher, false);
    }

    public void markOutdatedInterests(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) return;
        if (teacher.getPreferredLat() == null || teacher.getPreferredLng() == null) return;
        if (teacher.getSubject() == null || teacher.getSchoolType() == null) return;

        List<TransferInterest> sentInterests = interestRepository.findByFromTeacherIdOrderByCreatedAtDesc(teacherId);
        for (TransferInterest interest : sentInterests) {
            if (interest.getTypeEnum() == InterestType.MUTUAL) continue;

            Teacher recipient = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
            if (recipient == null) continue;
            if (recipient.getCurrentLat() == null || recipient.getCurrentLng() == null) continue;

            boolean stillCompatible = true;

            if (!teacher.getSubject().equals(recipient.getSubject())) {
                stillCompatible = false;
            }

            if (stillCompatible && !teacher.getSchoolType().equals(recipient.getSchoolType())) {
                stillCompatible = false;
            }

            if (stillCompatible && teacher.getRadiusKm() != null) {
                double distance = haversineDistance(
                        teacher.getPreferredLat(), teacher.getPreferredLng(),
                        recipient.getCurrentLat(), recipient.getCurrentLng()
                );
                if (distance > teacher.getRadiusKm()) {
                    stillCompatible = false;
                }
            }

            boolean wasOutdated = Boolean.TRUE.equals(interest.getIsOutdated());
            if (!stillCompatible && !wasOutdated) {
                interest.setIsOutdated(true);
                interestRepository.save(interest);
            } else if (stillCompatible && wasOutdated) {
                interest.setIsOutdated(false);
                interestRepository.save(interest);
            }
        }
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
    }

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<InterestResponse> getSentInterests(Long teacherId) {
        List<TransferInterest> interests = interestRepository.findByFromTeacherIdOrderByCreatedAtDesc(teacherId);
        return interests.stream()
                .map(interest -> {
                    Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
                    Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
                    boolean isMutual = interest.getStatus().equals(InterestStatus.ACCEPTED.getCode());
                    return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual);
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
                    return mapToInterestResponse(interest, fromTeacher, toTeacher, isMutual);
                })
                .collect(Collectors.toList());
    }

    private InterestResponse mapToInterestResponse(TransferInterest interest, Teacher fromTeacher, Teacher toTeacher, boolean isMutual) {
        InterestResponse response = new InterestResponse();
        response.setId(interest.getId());
        response.setFromTeacherId(interest.getFromTeacherId());
        response.setToTeacherId(interest.getToTeacherId());
        response.setType(InterestType.fromCode(interest.getType()));
        response.setStatus(InterestStatus.fromCode(interest.getStatus()));
        response.setOutdated(Boolean.TRUE.equals(interest.getIsOutdated()));
        response.setOutdatedReason(Boolean.TRUE.equals(interest.getIsOutdated())
                ? "Based on your previous preferences" : null);
        response.setCreatedAt(interest.getCreatedAt());
        response.setRespondedAt(interest.getRespondedAt());

        if (fromTeacher != null) {
            if (isMutual) {
                response.setFromTeacherName(fromTeacher.getName());
                response.setFromTeacherPhone(fromTeacher.getPhone());
                response.setFromTeacherSchool(fromTeacher.getSchoolName());
            } else {
                response.setFromTeacherName(fromTeacher.getName());
            }
        }

        if (toTeacher != null) {
            if (isMutual) {
                response.setToTeacherName(toTeacher.getName());
                response.setToTeacherPhone(toTeacher.getPhone());
                response.setToTeacherSchool(toTeacher.getSchoolName());
            } else {
                response.setToTeacherName(toTeacher.getName());
            }
        }

        return response;
    }
}
