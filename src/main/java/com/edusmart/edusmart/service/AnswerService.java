package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Answer;
import com.edusmart.edusmart.model.Question;
import com.edusmart.edusmart.repository.AnswerRepository;
import com.edusmart.edusmart.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Optional<Answer> getAnswerById(UUID id) {
        return answerRepository.findById(id);
    }

    public Answer createAnswer(Answer answer, UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        answer.setQuestion(question);
        return answerRepository.save(answer);
    }

    public Answer updateAnswer(UUID id, Answer updatedAnswer, UUID questionId) {
        return answerRepository.findById(id)
                .map(answer -> {
                    Question question = questionRepository.findById(questionId)
                            .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
                    answer.setAnswerText(updatedAnswer.getAnswerText());
                    answer.setIsCorrect(updatedAnswer.getIsCorrect());
                    answer.setQuestion(question);
                    return answerRepository.save(answer);
                })
                .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + id));
    }

    public void deleteAnswer(UUID id) {
        answerRepository.deleteById(id);
    }

    public List<Answer> getAnswersByQuestionId(UUID questionId) {
        return answerRepository.findByQuestionId(questionId);
    }
}