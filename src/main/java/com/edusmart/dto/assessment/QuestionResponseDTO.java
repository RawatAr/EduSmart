package com.edusmart.dto.assessment;

import com.edusmart.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for question response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDTO {
    
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private Integer marks;
    private Integer orderNumber;
    private String explanation;
    
    // Options (for MCQ/True-False)
    private List<QuestionOptionResponseDTO> options;
    
    // Model answer (for instructors only)
    private String modelAnswer;
}
