package com.edusmart.dto.assessment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for question options
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionRequestDTO {
    
    @NotBlank(message = "Option text is required")
    private String optionText;
    
    private Boolean isCorrect;
    
    private Integer orderNumber;
}
