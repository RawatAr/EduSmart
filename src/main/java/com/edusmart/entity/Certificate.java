package com.edusmart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Certificate entity for course completion
 */
@Entity
@Table(name = "certificates", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student", "course"})
@ToString(exclude = {"student", "course"})
public class Certificate extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Column(nullable = false, unique = true)
    private String certificateNumber;
    
    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;
    
    @Column(name = "completion_date", nullable = false)
    private LocalDateTime completionDate;
    
    @Column(name = "final_grade")
    private Double finalGrade;
    
    @Column(name = "certificate_url")
    private String certificateUrl; // PDF storage location
    
    @Column(name = "verification_code", unique = true)
    private String verificationCode; // For public verification
}
