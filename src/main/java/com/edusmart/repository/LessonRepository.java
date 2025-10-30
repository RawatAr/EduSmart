package com.edusmart.repository;

import com.edusmart.entity.Course;
import com.edusmart.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Lesson entity
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    List<Lesson> findByCourseIdOrderByLessonOrderAsc(Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId ORDER BY l.lessonOrder ASC")
    List<Lesson> findByCourseIdOrdered(Long courseId);
    
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    // Day 3: Entity-based count method
    int countByCourse(Course course);
    
    // Day 8: Analytics method
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.instructor.id = :instructorId")
    Integer countByInstructorId(Long instructorId);
}
