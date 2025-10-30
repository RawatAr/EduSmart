package com.edusmart.dto.assessment;

import com.edusmart.entity.enums.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating questions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestDTO {
    
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    @NotNull(message = "Question type is required")
    private QuestionType questionType;
    
    @Min(value = 0, message = "Marks must be positive")
    private Integer marks;
    
    private Integer orderNumber;
    
    private String explanation;
    
    // For MCQ/True-False
    private List<QuestionOptionRequestDTO> options;
    
    // For subjective questions
    private String modelAnswer;
}
