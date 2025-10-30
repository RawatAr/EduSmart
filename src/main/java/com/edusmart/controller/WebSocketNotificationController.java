package com.edusmart.controller;

import com.edusmart.dto.notification.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for Real-Time Notifications
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationController {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        log.info("Sending real-time notification to user: {}", userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }
    
    /**
     * Send progress update to user
     */
    public void sendProgressUpdate(Long userId, Object progressUpdate) {
        log.info("Sending progress update to user: {}", userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/progress",
            progressUpdate
        );
    }
    
    /**
     * Broadcast system message to all users
     */
    public void broadcastSystemMessage(String message) {
        log.info("Broadcasting system message: {}", message);
        messagingTemplate.convertAndSend("/topic/system", message);
    }
    
    /**
     * Send course update to enrolled students
     */
    public void sendCourseUpdate(Long courseId, Object update) {
        log.info("Sending course update for course: {}", courseId);
        messagingTemplate.convertAndSend("/topic/course/" + courseId, update);
    }
    
    /**
     * Handle client ping for connection keep-alive
     */
    @MessageMapping("/ping")
    public void handlePing(@Payload String message) {
        log.debug("Received ping: {}", message);
    }
}
