package com.edusmart.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for instructor dashboard analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorAnalyticsDTO {
    
    private Long instructorId;
    private String instructorName;
    
    // Course metrics
    private Integer totalCourses;
    private Integer publishedCourses;
    private Integer draftCourses;
    
    // Student metrics
    private Long totalStudents;
    private Long activeStudents;
    private Long newStudentsThisMonth;
    
    // Content metrics
    private Integer totalLessons;
    private Integer totalAssessments;
    private Integer totalDiscussions;
    
    // Engagement metrics
    private Long totalEnrollments;
    private Double averageCourseRating;
    private Long totalReviews;
    
    // Revenue metrics
    private Double totalRevenue;
    private Double revenueThisMonth;
    
    // Performance metrics
    private Double averageCompletionRate;
    private Double averageStudentGrade;
}
