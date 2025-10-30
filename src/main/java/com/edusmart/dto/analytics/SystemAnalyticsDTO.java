package com.edusmart.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for system-wide analytics (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAnalyticsDTO {
    
    // User metrics
    private Long totalUsers;
    private Long totalStudents;
    private Long totalInstructors;
    private Long totalAdmins;
    private Long newUsersThisMonth;
    private Long activeUsersThisMonth;
    
    // Course metrics
    private Long totalCourses;
    private Long publishedCourses;
    private Long totalCategories;
    
    // Enrollment metrics
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    private Double overallCompletionRate;
    
    // Content metrics
    private Long totalLessons;
    private Long totalAssessments;
    private Long totalQuestions;
    private Long totalDiscussions;
    
    // Engagement metrics
    private Long totalSubmissions;
    private Long totalCompletions;
    private Long totalNotifications;
    private Double averageGrade;
    
    // Revenue metrics
    private Double totalRevenue;
    private Double revenueThisMonth;
    private Double revenueThisYear;
}
