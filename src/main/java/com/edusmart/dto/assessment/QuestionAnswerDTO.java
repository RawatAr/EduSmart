package com.edusmart.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual question answers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAnswerDTO {
    
    private Long questionId;
    private Integer questionNumber;
    private String questionType; // MULTIPLE_CHOICE, SHORT_ANSWER, TRUE_FALSE, ESSAY
    private String studentAnswer;
    private String correctAnswer;
    private Double points;
}
