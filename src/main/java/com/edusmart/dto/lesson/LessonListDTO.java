package com.edusmart.dto.lesson;

import com.edusmart.entity.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for lesson list view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonListDTO {
    
    private Long id;
    private String title;
    private LessonType lessonType;
    private Integer durationMinutes;
    private Integer lessonOrder;
    private Boolean isFree;
    private Boolean isCompleted;
}
