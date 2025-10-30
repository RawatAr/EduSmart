package com.edusmart.repository;

import com.edusmart.entity.Category;
import com.edusmart.entity.Course;
import com.edusmart.entity.CourseLevel;
import com.edusmart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Course entity
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByInstructor(User instructor);
    
    List<Course> findByInstructorId(Long instructorId);
    
    List<Course> findByCategoryId(Long categoryId);
    
    List<Course> findByIsPublished(Boolean isPublished);
    
    List<Course> findByLevel(CourseLevel level);
    
    // Day 3: Pagination and filtering methods
    Page<Course> findByIsPublished(Boolean isPublished, Pageable pageable);
    
    Page<Course> findByCategoryAndIsPublished(Category category, Boolean isPublished, Pageable pageable);
    
    Page<Course> findByInstructor(User instructor, Pageable pageable);
    
    Page<Course> findByTitleContainingIgnoreCaseOrShortDescriptionContainingIgnoreCaseAndIsPublished(
            String title, String description, Boolean isPublished, Pageable pageable);
    
    List<Course> findByIsFeaturedAndIsPublished(Boolean isFeatured, Boolean isPublished);
    
    int countByCategory(Category category);
    
    @Query("SELECT c FROM Course c WHERE c.isPublished = true ORDER BY c.enrollmentCount DESC")
    List<Course> findPopularCourses(Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.isPublished = true ORDER BY c.createdAt DESC")
    List<Course> findLatestCourses(Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    long countByInstructorId(Long instructorId);
    
    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId AND c.isPublished = true")
    Page<Course> findByCategoryIdAndIsPublished(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Day 8: Analytics methods
    boolean existsByIdAndInstructorId(Long courseId, Long instructorId);
    
    long countByInstructorIdAndIsPublished(Long instructorId, boolean isPublished);
    
    long countByIsPublished(boolean isPublished);
}
