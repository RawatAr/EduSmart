package com.edusmart.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for top performing courses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCourseDTO {
    
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private Long enrollmentCount;
    private Double revenue;
    private Double averageRating;
    private Integer reviewCount;
}
