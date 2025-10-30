package com.edusmart.controller;

import com.edusmart.dto.analytics.*;
import com.edusmart.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for analytics and reporting
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Get course analytics (Instructor/Admin)
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseAnalyticsDTO> getCourseAnalytics(
            @PathVariable Long courseId,
            Authentication authentication) {
        CourseAnalyticsDTO analytics = analyticsService.getCourseAnalytics(courseId, authentication.getName());
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get student analytics
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<StudentAnalyticsDTO> getStudentAnalytics(@PathVariable Long studentId) {
        StudentAnalyticsDTO analytics = analyticsService.getStudentAnalytics(studentId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get my analytics (Student)
     */
    @GetMapping("/my-analytics")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAnalyticsDTO> getMyAnalytics(Authentication authentication) {
        // Get current user ID and fetch analytics
        return ResponseEntity.ok(new StudentAnalyticsDTO()); // TODO: Implement
    }
    
    /**
     * Get instructor dashboard analytics
     */
    @GetMapping("/instructor/dashboard")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<InstructorAnalyticsDTO> getInstructorDashboard(Authentication authentication) {
        InstructorAnalyticsDTO analytics = analyticsService.getInstructorAnalytics(authentication.getName());
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get system-wide analytics (Admin only)
     */
    @GetMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemAnalyticsDTO> getSystemAnalytics(Authentication authentication) {
        SystemAnalyticsDTO analytics = analyticsService.getSystemAnalytics(authentication.getName());
        return ResponseEntity.ok(analytics);
    }
}
