package com.edusmart.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for course analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAnalyticsDTO {
    
    private Long courseId;
    private String courseTitle;
    
    // Enrollment metrics
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    private Double completionRate;
    
    // Engagement metrics
    private Long totalLessons;
    private Long totalAssessments;
    private Long totalDiscussions;
    private Double averageProgress;
    
    // Performance metrics
    private Double averageGrade;
    private Integer totalSubmissions;
    private Integer passedStudents;
    private Integer failedStudents;
    
    // Revenue metrics (if applicable)
    private Double revenue;
    private Double averageRating;
    private Long totalReviews;
}
