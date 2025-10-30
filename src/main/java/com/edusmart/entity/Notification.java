package com.edusmart.entity;

import com.edusmart.entity.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Notification entity for user notifications
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(exclude = "user")
public class Notification extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Notification title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Notification message is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private NotificationType notificationType;
    
    // Alias for notificationType (for consistency with DTOs)
    public NotificationType getType() {
        return notificationType;
    }
    
    public void setType(NotificationType type) {
        this.notificationType = type;
    }
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "action_url")
    private String actionUrl;
    
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    public void markAsRead() {
        this.isRead = true;
    }
}
