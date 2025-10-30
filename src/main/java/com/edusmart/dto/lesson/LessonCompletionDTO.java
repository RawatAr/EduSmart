package com.edusmart.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for lesson completion tracking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCompletionDTO {
    
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long studentId;
    private String studentName;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
