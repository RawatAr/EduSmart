package com.edusmart.edusmart.dto;

import com.edusmart.edusmart.model.enums.AssessmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {

    private UUID id;

    @NotBlank(message = "Assessment title is required")
    private String title;

    private String description;

    @NotNull(message = "Assessment type is required")
    private AssessmentType type;

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    private String courseTitle;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @Positive(message = "Total points must be positive")
    private Integer totalPoints;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    // Additional fields for API responses
    private Integer questionCount;
    private Integer submissionCount;
    private Double averageScore;
}