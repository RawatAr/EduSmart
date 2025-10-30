package com.edusmart.dto.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for submitting an assessment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequestDTO {
    
    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;
    
    private List<AnswerDTO> answers;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerDTO {
        @NotNull(message = "Question ID is required")
        private Long questionId;
        
        // For MCQ/True-False
        private Long selectedOptionId;
        
        // For subjective
        private String answerText;
        
        // For file uploads
        private String fileUrl;
    }
}
