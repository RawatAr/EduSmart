package com.edusmart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Discussion entity representing forum topics
 */
@Entity
@Table(name = "discussions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"course", "user", "replies"})
@ToString(exclude = {"course", "user", "replies"})
public class Discussion extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
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
    
    @NotBlank(message = "Discussion title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Discussion content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    @Column(name = "is_pinned")
    private Boolean isPinned = false;
    
    @Column(name = "is_resolved")
    private Boolean isResolved = false;
    
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @JsonIgnore
    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussionReply> replies = new ArrayList<>();
    
    public void incrementViews() {
        this.viewsCount = (this.viewsCount == null ? 0 : this.viewsCount) + 1;
    }
}
