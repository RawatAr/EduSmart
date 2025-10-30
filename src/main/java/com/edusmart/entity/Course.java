package com.edusmart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Course entity representing educational courses
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"instructor", "category", "lessons", "enrollments", "assessments", "discussions"})
@ToString(exclude = {"instructor", "category", "lessons", "enrollments", "assessments", "discussions"})
public class Course extends BaseEntity {
    
    @NotBlank(message = "Course title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Course description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "short_description", length = 500)
    private String shortDescription;
    
    @Column(name = "full_description", length = 5000, columnDefinition = "TEXT")
    private String fullDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(length = 500)
    private String thumbnail;
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    @Column(name = "video_preview_url", length = 500)
    private String videoPreviewUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CourseLevel level;
    
    @Column(length = 50)
    private String language = "English";
    
    @Column(name = "duration_hours")
    @PositiveOrZero(message = "Duration must be positive or zero")
    private Integer durationHours;
    
    @Column(precision = 10, scale = 2)
    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal price = BigDecimal.ZERO;
    
    @Column(name = "is_published")
    private Boolean isPublished = false;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "enrollment_count")
    private Integer enrollmentCount = 0;
    
    @Column(length = 2000, columnDefinition = "TEXT")
    private String requirements;
    
    @Column(name = "target_audience", length = 1000, columnDefinition = "TEXT")
    private String targetAudience;
    
    @Column(name = "learning_objectives", length = 2000, columnDefinition = "TEXT")
    private String learningObjectives;
    
    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating = BigDecimal.ZERO;
    
    // Day 9: Review fields
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonOrder ASC")
    private List<Lesson> lessons = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assessment> assessments = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discussion> discussions = new ArrayList<>();
    
    // Utility methods
    public void incrementEnrollmentCount() {
        this.enrollmentCount = (this.enrollmentCount == null ? 0 : this.enrollmentCount) + 1;
    }
    
    public void decrementEnrollmentCount() {
        this.enrollmentCount = Math.max(0, (this.enrollmentCount == null ? 0 : this.enrollmentCount) - 1);
    }
}
