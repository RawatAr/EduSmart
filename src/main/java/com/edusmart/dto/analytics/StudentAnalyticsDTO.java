package com.edusmart.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for student analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalyticsDTO {
    
    private Long studentId;
    private String studentName;
    private String email;
    
    // Enrollment metrics
    private Integer totalEnrollments;
    private Integer activeEnrollments;
    private Integer completedCourses;
    
    // Progress metrics
    private Double overallProgress;
    private Integer totalLessonsCompleted;
    private Integer totalAssessmentsCompleted;
    
    // Performance metrics
    private Double averageGrade;
    private Integer totalSubmissions;
    private Integer passedAssessments;
    private Integer failedAssessments;
    
    // Engagement metrics
    private Integer totalDiscussions;
    private Integer totalReplies;
    private LocalDateTime lastActivity;
    private Integer daysActive;
}
