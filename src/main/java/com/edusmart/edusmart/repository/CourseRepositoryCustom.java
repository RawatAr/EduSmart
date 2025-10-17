package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CourseRepositoryCustom {

    /**
     * Find courses with optimized query including instructor and category information
     */
    Page<Course> findCoursesWithDetails(Pageable pageable);

    /**
     * Find courses by category with optimized join
     */
    List<Course> findByCategoryIdOptimized(UUID categoryId);

    /**
     * Find courses by instructor with optimized join
     */
    List<Course> findByInstructorIdOptimized(UUID instructorId);

    /**
     * Search courses with full-text search optimization
     */
    Page<Course> searchCourses(String query, UUID categoryId, Pageable pageable);

    /**
     * Find popular courses based on enrollment count
     */
    List<Course> findPopularCourses(int limit);

    /**
     * Find courses with enrollment statistics
     */
    List<Object[]> findCoursesWithEnrollmentStats();
}