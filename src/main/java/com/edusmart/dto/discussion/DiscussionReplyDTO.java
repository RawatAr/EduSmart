package com.edusmart.dto.discussion;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for discussion replies
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionReplyDTO {
    
    private Long id;
    
    @NotBlank(message = "Reply content is required")
    private String content;
    
    // Author info
    private Long authorId;
    private String authorName;
    private String authorRole;
    
    // Discussion reference
    private Long discussionId;
    
    // Parent reply (for nested replies)
    private Long parentReplyId;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
