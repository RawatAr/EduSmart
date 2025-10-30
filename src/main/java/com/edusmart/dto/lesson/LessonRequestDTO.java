package com.edusmart.dto.lesson;

import com.edusmart.entity.enums.LessonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating lessons
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequestDTO {
    
    @NotBlank(message = "Lesson title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Lesson type is required")
    private LessonType lessonType;
    
    private String content;
    
    private String videoUrl;
    
    @Min(value = 0, message = "Duration must be positive")
    private Integer durationMinutes;
    
    @NotNull(message = "Lesson order is required")
    @Min(value = 1, message = "Lesson order must start from 1")
    private Integer lessonOrder;
    
    private Boolean isFree;
    
    private String attachmentUrl;
}
