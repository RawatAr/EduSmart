package com.edusmart.repository;

import com.edusmart.entity.Assessment;
import com.edusmart.entity.enums.AssessmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Assessment entity
 */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    List<Assessment> findByCourseId(Long courseId);
    
    Page<Assessment> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);
    
    List<Assessment> findByAssessmentType(AssessmentType assessmentType);
    
    @Query("SELECT a FROM Assessment a WHERE a.course.id = :courseId AND a.isPublished = true")
    List<Assessment> findPublishedAssessmentsByCourseId(Long courseId);
    
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    // Day 8: Analytics method
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.course.instructor.id = :instructorId")
    Integer countByInstructorId(Long instructorId);
}
