package com.edusmart.dto.assessment;

import com.edusmart.entity.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for submission response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponseDTO {
    
    private Long id;
    
    // Assessment info
    private Long assessmentId;
    private String assessmentTitle;
    
    // Student info
    private Long studentId;
    private String studentName;
    
    // Submission details
    private SubmissionStatus status;
    private Integer obtainedMarks;
    private Integer totalMarks;
    private Double percentage;
    private Boolean passed;
    
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    
    // Answers (if showing results)
    private List<AnswerResultDTO> answers;
    
    // Feedback
    private String feedback;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerResultDTO {
        private Long questionId;
        private String questionText;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Integer marksObtained;
        private Integer maxMarks;
    }
}
