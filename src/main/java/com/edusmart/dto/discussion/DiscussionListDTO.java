package com.edusmart.dto.discussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for discussion list view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionListDTO {
    
    private Long id;
    private String title;
    private String excerpt; // First 150 chars of content
    
    private Long authorId;
    private String authorName;
    
    private Long courseId;
    private String courseTitle;
    
    private Integer replyCount;
    private Boolean isPinned;
    private Boolean isResolved;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
}
