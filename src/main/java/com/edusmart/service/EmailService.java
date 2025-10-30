package com.edusmart.service;

import com.edusmart.dto.email.EmailRequestDTO;
import com.edusmart.entity.enums.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@edusmart.com}")
    private String fromEmail;

    @Value("${app.name:EduSmart}")
    private String appName;

    /**
     * Send email asynchronously
     */
    @Async
    public void sendEmail(EmailRequestDTO request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(buildEmailContent(request.getTemplate(), request.getVariables()), true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", request.getTo(), e);
        }
    }

    /**
     * Send order confirmation email
     */
    @Async
    public void sendOrderConfirmationEmail(String to, String orderNumber, String customerName, Double totalAmount) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "orderNumber", orderNumber,
            "totalAmount", String.format("%.2f", totalAmount),
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Order Confirmation - " + orderNumber)
            .template(EmailTemplate.ORDER_CONFIRMATION.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Send invoice email
     */
    @Async
    public void sendInvoiceEmail(String to, String invoiceNumber, String customerName, Double totalAmount) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "invoiceNumber", invoiceNumber,
            "totalAmount", String.format("%.2f", totalAmount),
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Invoice - " + invoiceNumber)
            .template(EmailTemplate.INVOICE.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Send enrollment confirmation email
     */
    @Async
    public void sendEnrollmentConfirmationEmail(String to, String studentName, String courseTitle) {
        Map<String, Object> variables = Map.of(
            "studentName", studentName,
            "courseTitle", courseTitle,
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Enrollment Confirmation - " + courseTitle)
            .template(EmailTemplate.ENROLLMENT_CONFIRMATION.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Send welcome email
     */
    @Async
    public void sendWelcomeEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Welcome to " + appName + "!")
            .template(EmailTemplate.WELCOME.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Send certificate issued email
     */
    @Async
    public void sendCertificateIssuedEmail(String to, String studentName, String courseTitle, String certificateUrl) {
        Map<String, Object> variables = Map.of(
            "studentName", studentName,
            "courseTitle", courseTitle,
            "certificateUrl", certificateUrl,
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Certificate Issued - " + courseTitle)
            .template(EmailTemplate.CERTIFICATE_ISSUED.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Send password reset email
     */
    @Async
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "resetToken", resetToken,
            "appName", appName
        );

        EmailRequestDTO request = EmailRequestDTO.builder()
            .to(to)
            .subject("Password Reset Request")
            .template(EmailTemplate.PASSWORD_RESET.name())
            .variables(variables)
            .build();

        sendEmail(request);
    }

    /**
     * Build email content from template
     */
    private String buildEmailContent(String templateName, Map<String, Object> variables) {
        // In production, use a template engine like Thymeleaf or FreeMarker
        // For now, return simple HTML templates
        
        switch (EmailTemplate.valueOf(templateName)) {
            case ORDER_CONFIRMATION:
                return buildOrderConfirmationTemplate(variables);
            case INVOICE:
                return buildInvoiceTemplate(variables);
            case ENROLLMENT_CONFIRMATION:
                return buildEnrollmentTemplate(variables);
            case WELCOME:
                return buildWelcomeTemplate(variables);
            case CERTIFICATE_ISSUED:
                return buildCertificateTemplate(variables);
            case PASSWORD_RESET:
                return buildPasswordResetTemplate(variables);
            default:
                return buildDefaultTemplate(variables);
        }
    }

    private String buildOrderConfirmationTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Order Confirmation</h2>
                <p>Dear %s,</p>
                <p>Thank you for your order! Your order has been confirmed.</p>
                <p><strong>Order Number:</strong> %s</p>
                <p><strong>Total Amount:</strong> $%s</p>
                <p>You can access your courses from your dashboard.</p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """, 
            vars.get("customerName"), 
            vars.get("orderNumber"), 
            vars.get("totalAmount"),
            vars.get("appName")
        );
    }

    private String buildInvoiceTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Invoice</h2>
                <p>Dear %s,</p>
                <p>Please find your invoice details below:</p>
                <p><strong>Invoice Number:</strong> %s</p>
                <p><strong>Total Amount:</strong> $%s</p>
                <p>Thank you for your business!</p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """, 
            vars.get("customerName"), 
            vars.get("invoiceNumber"), 
            vars.get("totalAmount"),
            vars.get("appName")
        );
    }

    private String buildEnrollmentTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Enrollment Confirmation</h2>
                <p>Dear %s,</p>
                <p>Congratulations! You have been successfully enrolled in:</p>
                <h3>%s</h3>
                <p>You can start learning right away from your dashboard.</p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """, 
            vars.get("studentName"), 
            vars.get("courseTitle"),
            vars.get("appName")
        );
    }

    private String buildWelcomeTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Welcome to %s!</h2>
                <p>Dear %s,</p>
                <p>Thank you for joining us! We're excited to have you on board.</p>
                <p>Start exploring our courses and begin your learning journey today.</p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """,
            vars.get("appName"),
            vars.get("userName"),
            vars.get("appName")
        );
    }

    private String buildCertificateTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Certificate Issued!</h2>
                <p>Dear %s,</p>
                <p>Congratulations! You have earned a certificate for completing:</p>
                <h3>%s</h3>
                <p><a href="%s">Download Your Certificate</a></p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """, 
            vars.get("studentName"), 
            vars.get("courseTitle"),
            vars.get("certificateUrl"),
            vars.get("appName")
        );
    }

    private String buildPasswordResetTemplate(Map<String, Object> vars) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Password Reset Request</h2>
                <p>Dear %s,</p>
                <p>We received a request to reset your password.</p>
                <p>Your reset token: <strong>%s</strong></p>
                <p>If you didn't request this, please ignore this email.</p>
                <p>Best regards,<br>%s Team</p>
            </body>
            </html>
            """, 
            vars.get("userName"), 
            vars.get("resetToken"),
            vars.get("appName")
        );
    }

    private String buildDefaultTemplate(Map<String, Object> vars) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <p>You have received a notification.</p>
            </body>
            </html>
            """;
    }
}
