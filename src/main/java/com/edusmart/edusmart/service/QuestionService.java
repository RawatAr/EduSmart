package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Assessment;
import com.edusmart.edusmart.model.Question;
import com.edusmart.edusmart.repository.AssessmentRepository;
import com.edusmart.edusmart.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(UUID id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(Question question, UUID assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
        question.setAssessment(assessment);
        return questionRepository.save(question);
    }

    public Question updateQuestion(UUID id, Question updatedQuestion, UUID assessmentId) {
        return questionRepository.findById(id)
                .map(question -> {
                    Assessment assessment = assessmentRepository.findById(assessmentId)
                            .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
                    question.setQuestionText(updatedQuestion.getQuestionText());
                    question.setQuestionType(updatedQuestion.getQuestionType());
                    question.setAssessment(assessment);
                    return questionRepository.save(question);
                })
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));
    }

    public void deleteQuestion(UUID id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getQuestionsByAssessmentId(UUID assessmentId) {
        return questionRepository.findByAssessmentId(assessmentId);
    }
}