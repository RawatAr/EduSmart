package com.edusmart.edusmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CourseDTO {

    private UUID id;

    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 200, message = "Course title must be between 3 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String content;

    @NotNull(message = "Instructor ID is required")
    private UUID instructorId;

    private String instructorName;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private String categoryName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for API responses
    private Integer enrollmentCount;
    private Double averageRating;
    private Integer lessonCount;
    private Integer assessmentCount;
}