package com.edusmart.dto.course;

import com.edusmart.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for enrollment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {
    
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseThumbnail;
    private Long studentId;
    private String studentName;
    private EnrollmentStatus status;
    private Integer progress;
    private Boolean isCompleted;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
    
    // Embedded course details for frontend
    private Map<String, Object> course;
}
