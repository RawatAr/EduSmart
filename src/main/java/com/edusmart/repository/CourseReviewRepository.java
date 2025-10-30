package com.edusmart.repository;

import com.edusmart.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CourseReview entity
 */
@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    
    Optional<CourseReview> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    Page<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);
    
    List<CourseReview> findByStudentId(Long studentId);
    
    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.id = :courseId")
    Double getAverageRatingByCourseId(Long courseId);
    
    @Query("SELECT COUNT(r) FROM CourseReview r WHERE r.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    @Query("SELECT r FROM CourseReview r WHERE r.course.id = :courseId AND r.isVerified = true ORDER BY r.helpfulCount DESC, r.createdAt DESC")
    Page<CourseReview> findVerifiedReviewsByCourseId(Long courseId, Pageable pageable);
}
