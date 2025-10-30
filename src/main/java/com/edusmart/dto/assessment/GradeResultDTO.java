package com.edusmart.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for grade results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResultDTO {
    
    private Long gradeId;
    private Long assessmentId;
    private Long studentId;
    private Double score;
    private Double maxScore;
    private Double percentage;
    private String feedback;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Boolean passed;
    private LocalDateTime gradedAt;
}
