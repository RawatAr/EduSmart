package com.edusmart.dto.course;

import com.edusmart.entity.CourseLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating and updating courses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    
    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @NotBlank(message = "Short description is required")
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;
    
    @NotBlank(message = "Full description is required")
    @Size(min = 50, message = "Full description must be at least 50 characters")
    private String fullDescription;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    @NotNull(message = "Course level is required")
    private CourseLevel level;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    private String thumbnailUrl;
    
    private String videoPreviewUrl;
    
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationHours;
    
    private String language;
    
    private String requirements;
    
    private String targetAudience;
    
    private String learningObjectives;
    
    private Boolean isPublished;
}
