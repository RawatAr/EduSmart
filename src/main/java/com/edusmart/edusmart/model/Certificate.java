package com.edusmart.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificate")
@Data
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "certificate_number", nullable = false, unique = true)
    private String certificateNumber;

    @CreationTimestamp
    @Column(name = "issued_date", nullable = false, updatable = false)
    private LocalDateTime issuedDate;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "grade")
    private String grade;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;
}