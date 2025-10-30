package com.edusmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Course review and rating entity
 */
@Entity
@Table(name = "course_reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student", "course"})
@ToString(exclude = {"student", "course"})
public class CourseReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;
    
    @Size(max = 1000, message = "Review must not exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String reviewText;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false; // Only students who completed the course
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
}
