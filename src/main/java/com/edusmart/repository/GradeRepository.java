package com.edusmart.repository;

import com.edusmart.entity.Assessment;
import com.edusmart.entity.Grade;
import com.edusmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Grade entity
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    Optional<Grade> findByAssessmentAndStudent(Assessment assessment, User student);
    
    List<Grade> findByStudent(User student);
    
    List<Grade> findByAssessment(Assessment assessment);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId ORDER BY g.gradedAt DESC")
    List<Grade> findByStudentIdOrderByGradedAtDesc(Long studentId);
    
    @Query("SELECT AVG(g.percentage) FROM Grade g WHERE g.student.id = :studentId")
    Double getAverageGradeForStudent(Long studentId);
}
