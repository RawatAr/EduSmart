package com.edusmart.repository;

import com.edusmart.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LessonCompletion entity
 */
@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    
    List<LessonCompletion> findByStudentId(Long studentId);
    
    List<LessonCompletion> findByLessonId(Long lessonId);
    
    Optional<LessonCompletion> findByLessonIdAndStudentId(Long lessonId, Long studentId);
    
    boolean existsByLessonIdAndStudentId(Long lessonId, Long studentId);
    
    // Day 4: Additional query methods
    Optional<LessonCompletion> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    
    @Query("SELECT lc FROM LessonCompletion lc WHERE lc.student.id = :studentId AND lc.lesson.course.id = :courseId")
    List<LessonCompletion> findByStudentIdAndLessonCourseId(Long studentId, Long courseId);
    
    @Query("SELECT lc FROM LessonCompletion lc WHERE lc.lesson.course.id = :courseId AND lc.student.id = :studentId")
    List<LessonCompletion> findByCourseIdAndStudentId(Long courseId, Long studentId);
    
    @Query("SELECT COUNT(lc) FROM LessonCompletion lc WHERE lc.lesson.course.id = :courseId AND lc.student.id = :studentId")
    long countCompletedLessonsByCourseAndStudent(Long courseId, Long studentId);
    
    // Day 8: Analytics method
    long countByStudentId(Long studentId);
}
