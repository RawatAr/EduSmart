package com.edusmart.repository;

import com.edusmart.entity.Submission;
import com.edusmart.entity.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Submission entity
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByStudentId(Long studentId);
    
    List<Submission> findByAssessmentId(Long assessmentId);
    
    List<Submission> findByStatus(SubmissionStatus status);
    
    Optional<Submission> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assessment.id = :assessmentId AND s.student.id = :studentId ORDER BY s.submittedAt DESC")
    List<Submission> findByAssessmentIdAndStudentIdOrderBySubmittedAtDesc(Long assessmentId, Long studentId);
    
    Page<Submission> findByAssessmentIdOrderBySubmittedAtDesc(Long assessmentId, Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assessment.id = :assessmentId AND s.student.id = :studentId")
    long countByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assessment.id = :assessmentId")
    long countByAssessmentId(Long assessmentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.id = :studentId")
    long countByStudentId(Long studentId);
    
    // Day 8: Analytics methods
    @Query("SELECT AVG(s.obtainedMarks) FROM Submission s WHERE s.assessment.course.id = :courseId")
    Double getAverageGradeByCourseId(Long courseId);
    
    @Query("SELECT AVG(s.obtainedMarks) FROM Submission s WHERE s.student.id = :studentId")
    Double getAverageGradeByStudentId(Long studentId);
    
    @Query("SELECT AVG(s.obtainedMarks) FROM Submission s WHERE s.assessment.course.instructor.id = :instructorId")
    Double getAverageGradeByInstructorId(Long instructorId);
    
    @Query("SELECT AVG(s.obtainedMarks) FROM Submission s")
    Double getAverageGrade();
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assessment.course.id = :courseId")
    Integer countByCourseId(Long courseId);
}
