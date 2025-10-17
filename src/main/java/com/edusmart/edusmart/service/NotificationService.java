package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Notification;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.NotificationType;
import com.edusmart.edusmart.repository.NotificationRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public long getUnreadNotificationCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public Notification createNotification(UUID userId, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            savedNotification
        );

        return savedNotification;
    }

    public Notification markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Helper methods for common notification types
    public void notifyCourseEnrollment(UUID studentId, String courseTitle) {
        String message = "You have successfully enrolled in the course: " + courseTitle;
        createNotification(studentId, message, NotificationType.COURSE_ENROLLMENT);
    }

    public void notifyAssessmentGraded(UUID studentId, String assessmentTitle, int score) {
        String message = "Your assessment '" + assessmentTitle + "' has been graded. Score: " + score;
        createNotification(studentId, message, NotificationType.ASSESSMENT_GRADED);
    }

    public void notifyNewCourseMaterial(UUID studentId, String courseTitle) {
        String message = "New material has been added to the course: " + courseTitle;
        createNotification(studentId, message, NotificationType.NEW_MATERIAL);
    }

    public void notifyProgressUpdate(UUID studentId, String courseTitle, double progress) {
        String message = "Your progress in '" + courseTitle + "' is now " + String.format("%.1f", progress) + "%";
        createNotification(studentId, message, NotificationType.PROGRESS_UPDATE);
    }
}