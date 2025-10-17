package com.edusmart.edusmart.controller.v1;

import com.edusmart.edusmart.dto.AnalyticsDTO;
import com.edusmart.edusmart.dto.CertificateDTO;
import com.edusmart.edusmart.service.AnalyticsService;
import com.edusmart.edusmart.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Reporting", description = "APIs for analytics, reporting, and certificates")
public class AnalyticsControllerV1 {

    private final AnalyticsService analyticsService;
    private final CertificateService certificateService;

    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get platform analytics", description = "Retrieve comprehensive platform analytics and metrics")
    public ResponseEntity<AnalyticsDTO> getPlatformAnalytics() {
        AnalyticsDTO analytics = analyticsService.getPlatformAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/users/{userId}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get user progress analytics", description = "Retrieve progress analytics for a specific user")
    public ResponseEntity<Map<String, Object>> getUserProgressAnalytics(@PathVariable UUID userId) {
        Map<String, Object> analytics = analyticsService.getUserProgressAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/courses/{courseId}/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Get course analytics", description = "Retrieve analytics for a specific course")
    public ResponseEntity<Map<String, Object>> getCourseAnalytics(@PathVariable UUID courseId) {
        Map<String, Object> analytics = analyticsService.getCourseAnalytics(courseId);
        return ResponseEntity.ok(analytics);
    }

    // Certificate endpoints
    @PostMapping("/certificates/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Generate certificate", description = "Generate a certificate for course completion")
    public ResponseEntity<CertificateDTO> generateCertificate(
            @RequestParam UUID userId,
            @RequestParam UUID courseId) {
        CertificateDTO certificate = certificateService.generateCertificate(userId, courseId);
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/certificates/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get user certificates", description = "Retrieve all certificates for a user")
    public ResponseEntity<List<CertificateDTO>> getUserCertificates(@PathVariable UUID userId) {
        List<CertificateDTO> certificates = certificateService.getUserCertificates(userId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/certificates/{certificateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Get certificate by ID", description = "Retrieve a specific certificate by ID")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable UUID certificateId) {
        return certificateService.getCertificate(certificateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/certificates/user/{userId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get certificate by user and course", description = "Retrieve certificate for specific user and course")
    public ResponseEntity<CertificateDTO> getCertificateByUserAndCourse(
            @PathVariable UUID userId,
            @PathVariable UUID courseId) {
        return certificateService.getCertificateByUserAndCourse(userId, courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/certificates/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Get certificates by course", description = "Retrieve all certificates for a course")
    public ResponseEntity<List<CertificateDTO>> getCertificatesByCourse(@PathVariable UUID courseId) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByCourse(courseId);
        return ResponseEntity.ok(certificates);
    }
}