package com.teachertransfer.service;

import com.teachertransfer.dto.notification.NotificationResponse;
import com.teachertransfer.entity.Notification;
import com.teachertransfer.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(Long teacherId) {
        List<Notification> notifications = notificationRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications(Long teacherId) {
        List<Notification> notifications = notificationRepository.findUnreadByTeacherId(teacherId);
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long teacherId) {
        return notificationRepository.countUnreadByTeacherId(teacherId);
    }

    public void markAsRead(Long notificationId, Long teacherId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Check if notification belongs to teacher
        if (!notification.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Notification does not belong to this teacher");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long teacherId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadByTeacherId(teacherId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public void createNotification(Long teacherId, String title, String message, String type,
                                   Long relatedTeacherId, Long relatedInterestId) {
        Notification notification = new Notification();
        notification.setTeacherId(teacherId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedTeacherId(relatedTeacherId);
        notification.setRelatedInterestId(relatedInterestId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.getRead());
        response.setRelatedTeacherId(notification.getRelatedTeacherId());
        response.setRelatedInterestId(notification.getRelatedInterestId());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}