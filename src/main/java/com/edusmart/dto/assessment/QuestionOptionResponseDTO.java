package com.edusmart.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for question option response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionResponseDTO {
    
    private Long id;
    private String optionText;
    private Boolean isCorrect; // Only shown to instructors or after submission
    private Integer orderNumber;
}
