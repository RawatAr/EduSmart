package com.edusmart.dto.discussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for discussion response with details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionResponseDTO {
    
    private Long id;
    private String title;
    private String content;
    
    // Author info
    private Long authorId;
    private String authorName;
    private String authorRole;
    
    // Course info
    private Long courseId;
    private String courseTitle;
    
    // Lesson info (if linked)
    private Long lessonId;
    private String lessonTitle;
    
    // Stats
    private Integer replyCount;
    private Boolean isPinned;
    private Boolean isResolved;
    
    // Replies (if loaded)
    private List<DiscussionReplyDTO> replies;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
