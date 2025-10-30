package com.edusmart.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin dashboard statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    
    // User stats
    private Long totalUsers;
    private Long totalStudents;
    private Long totalInstructors;
    private Long totalAdmins;
    
    // Course stats
    private Long totalCourses;
    private Long publishedCourses;
    private Long draftCourses;
    
    // Enrollment stats
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    
    // Revenue stats
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Double todayRevenue;
    
    // Order stats
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long failedOrders;
    
    // Other stats
    private Long totalReviews;
    private Double averageCourseRating;
    private Long totalCertificates;
}
