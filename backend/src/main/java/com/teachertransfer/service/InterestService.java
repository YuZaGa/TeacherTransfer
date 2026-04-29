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

    public InterestResponse sendInterest(Long fromTeacherId, Long toTeacherId) {
        // Check if teachers exist
        Teacher fromTeacher = teacherRepository.findById(fromTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Teacher toTeacher = teacherRepository.findById(toTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Check if interest already exists
        if (interestRepository.findExistingInterest(fromTeacherId, toTeacherId).isPresent()) {
            throw new RuntimeException("Interest already sent");
        }

        // Create interest
        TransferInterest interest = new TransferInterest();
        interest.setFromTeacherId(fromTeacherId);
        interest.setToTeacherId(toTeacherId);
        interest.setType(InterestType.ONE_WAY.getCode());
        interest.setStatus(InterestStatus.PENDING.getCode());
        interest.setCreatedAt(LocalDateTime.now());

        interest = interestRepository.save(interest);

        // Create notification for recipient
        Notification notification = new Notification();
        notification.setTeacherId(toTeacherId);
        notification.setTitle("New Transfer Interest");
        notification.setMessage(fromTeacher.getName() + " is interested in transferring with you");
        notification.setType("INTEREST_RECEIVED");
        notification.setRelatedTeacherId(fromTeacherId);
        notification.setRelatedInterestId(interest.getId());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        return mapToInterestResponse(interest, fromTeacher, toTeacher);
    }

    public InterestResponse acceptInterest(Long interestId, Long teacherId) {
        TransferInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        // Check if teacher is the recipient
        if (!interest.getToTeacherId().equals(teacherId)) {
            throw new RuntimeException("You can only accept interests sent to you");
        }

        // Check if already responded
        if (interest.getStatus() != InterestStatus.PENDING.getCode()) {
            throw new RuntimeException("Interest already responded");
        }

        // Update interest status
        interest.setStatus(InterestStatus.ACCEPTED.getCode());
        interest.setRespondedAt(LocalDateTime.now());
        interest = interestRepository.save(interest);

        // Check for mutual interest
        List<TransferInterest> mutualInterests = interestRepository.findMutualInterests(
                interest.getToTeacherId(),
                interest.getFromTeacherId()
        );

        if (!mutualInterests.isEmpty()) {
            // Create notification for sender
            Teacher sender = teacherRepository.findById(interest.getFromTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            Notification notification = new Notification();
            notification.setTeacherId(interest.getFromTeacherId());
            notification.setTitle("Interest Accepted");
            notification.setMessage("Your transfer interest has been accepted!");
            notification.setType("INTEREST_ACCEPTED");
            notification.setRelatedTeacherId(interest.getToTeacherId());
            notification.setRelatedInterestId(interestId);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }

        Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return mapToInterestResponse(interest, fromTeacher, toTeacher);
    }

    public InterestResponse rejectInterest(Long interestId, Long teacherId) {
        TransferInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        // Check if teacher is the recipient
        if (!interest.getToTeacherId().equals(teacherId)) {
            throw new RuntimeException("You can only reject interests sent to you");
        }

        // Check if already responded
        if (interest.getStatus() != InterestStatus.PENDING.getCode()) {
            throw new RuntimeException("Interest already responded");
        }

        // Update interest status
        interest.setStatus(InterestStatus.REJECTED.getCode());
        interest.setRespondedAt(LocalDateTime.now());
        interest = interestRepository.save(interest);

        Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return mapToInterestResponse(interest, fromTeacher, toTeacher);
    }

    public List<InterestResponse> getSentInterests(Long teacherId) {
        List<TransferInterest> interests = interestRepository.findByFromTeacherIdOrderByCreatedAtDesc(teacherId);
        return interests.stream()
                .map(interest -> {
                    Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
                    Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
                    return mapToInterestResponse(interest, fromTeacher, toTeacher);
                })
                .collect(Collectors.toList());
    }

    public List<InterestResponse> getReceivedInterests(Long teacherId) {
        List<TransferInterest> interests = interestRepository.findByToTeacherIdOrderByCreatedAtDesc(teacherId);
        return interests.stream()
                .map(interest -> {
                    Teacher fromTeacher = teacherRepository.findById(interest.getFromTeacherId()).orElse(null);
                    Teacher toTeacher = teacherRepository.findById(interest.getToTeacherId()).orElse(null);
                    return mapToInterestResponse(interest, fromTeacher, toTeacher);
                })
                .collect(Collectors.toList());
    }

    private InterestResponse mapToInterestResponse(TransferInterest interest, Teacher fromTeacher, Teacher toTeacher) {
        InterestResponse response = new InterestResponse();
        response.setId(interest.getId());
        response.setFromTeacherId(interest.getFromTeacherId());
        response.setFromTeacherName(fromTeacher != null ? fromTeacher.getName() : null);
        response.setToTeacherId(interest.getToTeacherId());
        response.setToTeacherName(toTeacher != null ? toTeacher.getName() : null);
        response.setType(InterestType.fromCode(interest.getType()));
        response.setStatus(InterestStatus.fromCode(interest.getStatus()));
        response.setCreatedAt(interest.getCreatedAt());
        response.setRespondedAt(interest.getRespondedAt());
        return response;
    }
}