package com.edusmart.dto.course;

import com.edusmart.entity.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for course response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    
    private Long id;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String thumbnailUrl;
    private String videoPreviewUrl;
    private CourseLevel level;
    private BigDecimal price;
    private Integer durationHours;
    private String language;
    private Boolean isPublished;
    private Boolean isFeatured;
    
    // Category info
    private Long categoryId;
    private String categoryName;
    
    // Instructor info
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    
    // Statistics
    private Integer enrollmentCount;
    private Integer lessonCount;
    private Double averageRating;
    private Integer reviewCount;
    
    // Requirements and objectives
    private String requirements;
    private String targetAudience;
    private String learningObjectives;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
