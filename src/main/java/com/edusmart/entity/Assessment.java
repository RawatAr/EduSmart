package com.edusmart.entity;

import com.edusmart.entity.enums.AssessmentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Assessment entity representing quizzes, assignments, and exams
 */
@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"course", "questions", "submissions"})
@ToString(exclude = {"course", "questions", "submissions"})
public class Assessment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @NotBlank(message = "Assessment title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", length = 50)
    private AssessmentType assessmentType;
    
    @Column(name = "duration_minutes")
    @PositiveOrZero(message = "Duration must be positive or zero")
    private Integer durationMinutes;
    
    @Column(name = "passing_score", precision = 5, scale = 2)
    private BigDecimal passingScore;
    
    @Column(name = "total_points", precision = 10, scale = 2)
    private BigDecimal totalPoints;
    
    @Column(name = "total_marks")
    private Integer totalMarks;
    
    @Column(name = "passing_marks")
    private Integer passingMarks;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "max_attempts")
    private Integer maxAttempts;
    
    @Column(name = "show_results_immediately")
    private Boolean showResultsImmediately;
    
    @Column(name = "is_published")
    private Boolean isPublished = false;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    @JsonIgnore
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private List<Question> questions = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();
}
