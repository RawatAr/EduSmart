package com.edusmart.service;

import com.edusmart.entity.Notification;
import com.edusmart.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for delivering notifications via email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDeliveryService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /**
     * Deliver notification via email
     */
    @Async
    public void deliverNotificationViaEmail(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            
            if (notification == null) {
                log.warn("Notification not found: {}", notificationId);
                return;
            }

            String userEmail = notification.getUser().getEmail();
            String subject = notification.getTitle();
            String content = buildEmailContent(notification);

            // In a real implementation, you'd use proper email templates
            log.info("Delivering notification {} via email to {}", notificationId, userEmail);
            
            // For now, just log - actual email sending would happen here
            // emailService.sendEmail(...);
            
        } catch (Exception e) {
            log.error("Failed to deliver notification via email: {}", notificationId, e);
        }
    }

    private String buildEmailContent(Notification notification) {
        return String.format("""
            <html>
            <body>
                <h2>%s</h2>
                <p>%s</p>
            </body>
            </html>
            """,
            notification.getTitle(),
            notification.getMessage()
        );
    }
}
