package com.edusmart.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type; // Can be string or NotificationType
    private Boolean isRead;
    private String actionUrl;
    private LocalDateTime createdAt;
}
