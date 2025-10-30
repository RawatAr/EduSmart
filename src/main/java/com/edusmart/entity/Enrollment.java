package com.edusmart.entity;

import com.edusmart.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Enrollment entity representing student course enrollments
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"student", "course"})
@ToString(exclude = {"student", "course"})
public class Enrollment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    
    @Column(nullable = false)
    private Integer progress = 0;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "enrollment_date", nullable = false)
    private LocalDateTime enrollmentDate = LocalDateTime.now();
    
    @Column(name = "progress_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Progress must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Progress must be between 0 and 100")
    private BigDecimal progressPercentage = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
    
    @PrePersist
    protected void onCreate() {
        if (enrollmentDate == null) {
            enrollmentDate = LocalDateTime.now();
        }
    }
    
    // Utility methods
    public void updateProgress(BigDecimal progress) {
        this.progressPercentage = progress;
        this.lastAccessed = LocalDateTime.now();
        
        if (progress.compareTo(new BigDecimal("100")) >= 0 && !completed) {
            this.completed = true;
            this.completionDate = LocalDateTime.now();
        }
    }
    
    public void markAsAccessed() {
        this.lastAccessed = LocalDateTime.now();
    }
}
