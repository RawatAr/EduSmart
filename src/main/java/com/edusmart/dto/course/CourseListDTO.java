package com.edusmart.dto.course;

import com.edusmart.entity.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for course list/summary view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseListDTO {
    
    private Long id;
    private String title;
    private String shortDescription;
    private String thumbnailUrl;
    private CourseLevel level;
    private BigDecimal price;
    private Integer durationHours;
    private String language;
    private Boolean isPublished;
    private Boolean isFeatured;
    
    // Category
    private String categoryName;
    
    // Instructor
    private String instructorName;
    
    // Stats
    private Integer enrollmentCount;
    private Integer lessonCount;
    private Double averageRating;
    private Integer reviewCount;
}
