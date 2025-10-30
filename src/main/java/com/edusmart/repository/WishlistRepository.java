package com.edusmart.repository;

import com.edusmart.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Wishlist entity
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    Optional<Wishlist> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    Page<Wishlist> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.student.id = :studentId")
    long countByStudentId(Long studentId);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    void deleteByStudentIdAndCourseId(Long studentId, Long courseId);
}
