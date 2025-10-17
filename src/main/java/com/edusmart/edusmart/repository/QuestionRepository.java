package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByAssessmentId(UUID assessmentId);
}