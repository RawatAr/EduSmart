package com.edusmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DiscussionReply entity representing replies to discussion topics
 */
@Entity
@Table(name = "discussion_replies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"discussion", "user"})
@ToString(exclude = {"discussion", "user"})
public class DiscussionReply extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Alias for user (for consistency with DTOs)
    public User getAuthor() {
        return user;
    }
    
    public void setAuthor(User author) {
        this.user = author;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id")
    private DiscussionReply parentReply;
    
    @NotBlank(message = "Reply content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_answer")
    private Boolean isAnswer = false;
    
    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussionReply> childReplies = new ArrayList<>();
}
