package com.edusmart.dto.assessment;

import com.edusmart.entity.enums.AssessmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for assessment response with full details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private AssessmentType assessmentType;
    
    // Course info
    private Long courseId;
    private String courseTitle;
    
    // Lesson info (if linked to a lesson)
    private Long lessonId;
    private String lessonTitle;
    
    // Assessment settings
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isPublished;
    private Integer maxAttempts;
    private Boolean showResultsImmediately;
    
    // Questions (if loaded)
    private List<QuestionResponseDTO> questions;
    
    // Student info (if applicable)
    private Integer attemptsTaken;
    private Double bestScore;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
