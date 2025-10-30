package com.edusmart.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for enrollment trends over time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentTrendDTO {
    
    private String period; // e.g., "2025-01", "Week 42", etc.
    private Long enrollments;
    private Long completions;
    private Double completionRate;
    private Double averageGrade;
}
