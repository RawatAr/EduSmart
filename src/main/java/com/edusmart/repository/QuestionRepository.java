package com.edusmart.repository;

import com.edusmart.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Question entity
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByAssessmentIdOrderByQuestionOrderAsc(Long assessmentId);
}
