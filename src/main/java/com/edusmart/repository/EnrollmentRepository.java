package com.edusmart.repository;

import com.edusmart.entity.Course;
import com.edusmart.entity.Enrollment;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Enrollment entity
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    // Day 3: Entity-based methods with pagination
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    List<Enrollment> findByStudent(User student);
    
    Page<Enrollment> findByStudent(User student, Pageable pageable);
    
    Page<Enrollment> findByCourse(Course course, Pageable pageable);
    
    int countByCourse(Course course);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.completed = false")
    List<Enrollment> findInProgressEnrollmentsByStudentId(Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.completed = true")
    List<Enrollment> findCompletedEnrollmentsByStudentId(Long studentId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countByCourseId(Long courseId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId")
    long countByStudentId(Long studentId);
    
    // Day 8: Analytics methods
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    
    long countByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    
    long countByStatus(EnrollmentStatus status);
    
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.course.id = :courseId")
    Double getAverageProgressByCourseId(Long courseId);
    
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.student.id = :studentId")
    Double getAverageProgressByStudentId(Long studentId);
    
    @Query("SELECT MAX(e.lastAccessedAt) FROM Enrollment e WHERE e.student.id = :studentId")
    LocalDateTime getLastActivityByStudentId(Long studentId);
    
    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    Long countDistinctStudentsByInstructorId(Long instructorId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    Long countByInstructorId(Long instructorId);
    
    @Query("SELECT AVG(CASE WHEN e.status = 'COMPLETED' THEN 100.0 ELSE e.progress END) FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    Double getAverageCompletionRateByInstructorId(Long instructorId);
}
