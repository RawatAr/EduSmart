package com.edusmart.dto.assessment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for assessment submissions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSubmissionDTO {
    
    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotEmpty(message = "Answers cannot be empty")
    private List<QuestionAnswerDTO> answers;
    
    private String submissionText; // For essay/assignment submissions
    private List<String> fileUrls; // For file submissions
}
