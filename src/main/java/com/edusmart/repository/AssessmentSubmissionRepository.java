package com.edusmart.repository;

import com.edusmart.entity.Assessment;
import com.edusmart.entity.AssessmentSubmission;
import com.edusmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AssessmentSubmission entity
 */
@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {
    
    Optional<AssessmentSubmission> findByAssessmentAndStudent(Assessment assessment, User student);
    
    List<AssessmentSubmission> findByStudent(User student);
    
    List<AssessmentSubmission> findByAssessment(Assessment assessment);
    
    List<AssessmentSubmission> findByAssessmentOrderBySubmittedAtDesc(Assessment assessment);
}
