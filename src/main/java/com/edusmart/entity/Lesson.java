package com.edusmart.entity;

import com.edusmart.entity.enums.LessonType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Lesson entity representing course lessons/modules
 */
@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"course", "completions"})
@ToString(exclude = {"course", "completions"})
public class Lesson extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @NotBlank(message = "Lesson title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;
    
    @Column(name = "duration_minutes")
    @PositiveOrZero(message = "Duration must be positive or zero")
    private Integer durationMinutes;
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", length = 50)
    private LessonType lessonType;
    
    @Column(name = "is_preview")
    private Boolean isPreview = false;
    
    @Column(name = "is_free")
    private Boolean isFree = false;
    
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;
    
    @JsonIgnore
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonCompletion> completions = new ArrayList<>();
}
