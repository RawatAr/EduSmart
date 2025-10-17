package com.edusmart.edusmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {

    // Overall platform statistics
    private Long totalUsers;
    private Long totalCourses;
    private Long totalEnrollments;
    private Long totalAssessments;

    // User engagement metrics
    private Double averageCoursesPerUser;
    private Double averageAssessmentsPerUser;
    private Double completionRate;

    // Course performance metrics
    private Map<String, Long> enrollmentsByCategory;
    private Map<String, Double> averageRatingByCourse;

    // Time-based analytics
    private Map<String, Long> enrollmentsByMonth;
    private Map<String, Long> courseCompletionsByMonth;

    // Top performers
    private Map<String, Double> topRatedCourses;
    private Map<String, Long> mostEnrolledCourses;
    private Map<String, Double> highestScoringStudents;
}