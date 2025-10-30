package com.edusmart.dto.lesson;

import com.edusmart.entity.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for lesson response with full details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private LessonType lessonType;
    private String content;
    private String videoUrl;
    private Integer durationMinutes;
    private Integer lessonOrder;
    private Boolean isFree;
    private String attachmentUrl;
    
    // Course info
    private Long courseId;
    private String courseTitle;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Completion info (for students)
    private Boolean isCompleted;
    private LocalDateTime completedAt;
}
