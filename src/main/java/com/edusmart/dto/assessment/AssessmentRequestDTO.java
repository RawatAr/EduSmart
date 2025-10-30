package com.edusmart.dto.assessment;

import com.edusmart.entity.enums.AssessmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating/updating assessments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentRequestDTO {
    
    @NotBlank(message = "Assessment title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Assessment type is required")
    private AssessmentType assessmentType;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    private Long lessonId;
    
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;
    
    @Min(value = 0, message = "Total marks must be positive")
    private Integer totalMarks;
    
    @Min(value = 0, message = "Passing marks must be positive")
    private Integer passingMarks;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Boolean isPublished;
    
    private Integer maxAttempts;
    
    private Boolean showResultsImmediately;
}
