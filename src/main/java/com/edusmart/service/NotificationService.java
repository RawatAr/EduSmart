package com.edusmart.service;

import com.edusmart.controller.WebSocketNotificationController;
import com.edusmart.dto.notification.NotificationDTO;
import com.edusmart.entity.Notification;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.NotificationType;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.NotificationRepository;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing notifications with real-time WebSocket support
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationController webSocketController;
    
    /**
     * Create notification for a user
     */
    public NotificationDTO createNotification(Long userId, String title, String message, 
                                             NotificationType type, String actionUrl) {
        log.info("Creating notification for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .isRead(false)
                .actionUrl(actionUrl)
                .build();
        
        notification = notificationRepository.save(notification);
        
        NotificationDTO notificationDTO = mapToDTO(notification);
        
        // Send real-time notification via WebSocket
        try {
            webSocketController.sendNotificationToUser(userId, notificationDTO);
            log.info("Real-time notification sent to user: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to send real-time notification: {}", e.getMessage());
        }
        
        return notificationDTO;
    }
    
    /**
     * Get user notifications
     */
    public Page<NotificationDTO> getUserNotifications(String username, Pageable pageable) {
        log.info("Getting notifications for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        
        return notifications.map(this::mapToDTO);
    }
    
    /**
     * Get unread notifications count
     */
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }
    
    /**
     * Mark notification as read
     */
    public NotificationDTO markAsRead(Long notificationId, String username) {
        log.info("Marking notification {} as read", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        
        return mapToDTO(notification);
    }
    
    /**
     * Mark all notifications as read
     */
    public void markAllAsRead(String username) {
        log.info("Marking all notifications as read for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsRead(user.getId(), false);
        
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
    
    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId, String username) {
        log.info("Deleting notification: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        
        notificationRepository.delete(notification);
    }
    
    // Helper methods to create specific notifications
    
    public void notifyCourseEnrollment(Long studentId, String courseTitle) {
        createNotification(
            studentId,
            "Course Enrollment",
            "You have successfully enrolled in " + courseTitle,
            NotificationType.ENROLLMENT,
            "/courses"
        );
    }
    
    public void notifyNewLesson(Long studentId, String lessonTitle, String courseTitle) {
        createNotification(
            studentId,
            "New Lesson Available",
            "New lesson \"" + lessonTitle + "\" is available in " + courseTitle,
            NotificationType.COURSE_UPDATE,
            "/lessons"
        );
    }
    
    public void notifyAssessmentDue(Long studentId, String assessmentTitle, LocalDateTime dueDate) {
        createNotification(
            studentId,
            "Assessment Due Soon",
            "\"" + assessmentTitle + "\" is due soon",
            NotificationType.ASSIGNMENT,
            "/assessments"
        );
    }
    
    public void notifyAssessmentGraded(Long studentId, String assessmentTitle, Integer score) {
        createNotification(
            studentId,
            "Assessment Graded",
            "Your submission for \"" + assessmentTitle + "\" has been graded. Score: " + score,
            NotificationType.GRADE,
            "/assessments"
        );
    }
    
    public void notifyDiscussionReply(Long userId, String discussionTitle) {
        createNotification(
            userId,
            "New Reply",
            "Someone replied to your discussion \"" + discussionTitle + "\"",
            NotificationType.DISCUSSION,
            "/discussions"
        );
    }
    
    /**
     * Get notifications by user ID
     */
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        log.info("Getting notifications for user ID: {}", userId);
        
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Create notification from DTO
     */
    public NotificationDTO createNotification(NotificationDTO dto) {
        log.info("Creating notification from DTO for user: {}", dto.getUserId());
        
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        NotificationType type;
        try {
            type = NotificationType.valueOf(dto.getType().toUpperCase());
        } catch (Exception e) {
            type = NotificationType.INFO;
        }
        
        Notification notification = Notification.builder()
                .user(user)
                .title(dto.getTitle())
                .message(dto.getMessage())
                .notificationType(type)
                .isRead(dto.getIsRead() != null ? dto.getIsRead() : false)
                .actionUrl(dto.getActionUrl())
                .build();
        
        notification = notificationRepository.save(notification);
        
        return mapToDTO(notification);
    }
    
    /**
     * Mark notification as read by ID only
     */
    public void markAsReadById(Long notificationId) {
        log.info("Marking notification {} as read", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getNotificationType() != null ? notification.getNotificationType().name() : "INFO")
                .isRead(notification.getIsRead())
                .actionUrl(notification.getActionUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
