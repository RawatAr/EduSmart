package com.edusmart.dto.discussion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating discussions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionRequestDTO {
    
    @NotBlank(message = "Discussion title is required")
    private String title;
    
    @NotBlank(message = "Discussion content is required")
    private String content;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    private Long lessonId;
    
    private Boolean isPinned;
}
