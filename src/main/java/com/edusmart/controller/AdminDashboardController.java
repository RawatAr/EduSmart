package com.edusmart.controller;

import com.edusmart.dto.admin.DashboardStatsDTO;
import com.edusmart.dto.admin.TopCourseDTO;
import com.edusmart.dto.admin.TopInstructorDTO;
import com.edusmart.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for admin dashboard and analytics
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    
    private final AdminAnalyticsService analyticsService;
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get top performing courses
     */
    @GetMapping("/top-courses")
    public ResponseEntity<List<TopCourseDTO>> getTopCourses(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopCourseDTO> topCourses = analyticsService.getTopCourses(limit);
        return ResponseEntity.ok(topCourses);
    }
    
    /**
     * Get top performing instructors
     */
    @GetMapping("/top-instructors")
    public ResponseEntity<List<TopInstructorDTO>> getTopInstructors(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopInstructorDTO> topInstructors = analyticsService.getTopInstructors(limit);
        return ResponseEntity.ok(topInstructors);
    }
    
    /**
     * Get revenue for period
     */
    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double revenue = analyticsService.getRevenueForPeriod(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
    
    /**
     * Get total payments for period
     */
    @GetMapping("/payments")
    public ResponseEntity<Double> getTotalPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double payments = analyticsService.getTotalPayments(startDate, endDate);
        return ResponseEntity.ok(payments);
    }
}
