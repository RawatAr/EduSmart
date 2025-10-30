package com.edusmart.repository;

import com.edusmart.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Discussion entity
 */
@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    
    List<Discussion> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    Page<Discussion> findByCourseIdOrderByIsPinnedDescCreatedAtDesc(Long courseId, Pageable pageable);
    
    List<Discussion> findByUserId(Long userId);
    
    @Query("SELECT d FROM Discussion d WHERE d.course.id = :courseId ORDER BY d.isPinned DESC, d.createdAt DESC")
    List<Discussion> findByCourseIdOrderByPinnedAndCreatedAt(Long courseId);
    
    @Query("SELECT COUNT(d) FROM Discussion d WHERE d.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    // Day 8: Analytics methods
    long countByUserId(Long userId);
    
    @Query("SELECT COUNT(d) FROM Discussion d WHERE d.course.instructor.id = :instructorId")
    Integer countByInstructorId(Long instructorId);
}
