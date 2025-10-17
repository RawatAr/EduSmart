package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Answer;
import com.edusmart.edusmart.model.Assessment;
import com.edusmart.edusmart.model.Question;
import com.edusmart.edusmart.model.Submission;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.repository.AnswerRepository;
import com.edusmart.edusmart.repository.AssessmentRepository;
import com.edusmart.edusmart.repository.QuestionRepository;
import com.edusmart.edusmart.repository.SubmissionRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> getSubmissionById(UUID id) {
        return submissionRepository.findById(id);
    }

    public Submission createSubmission(Submission submission, UUID studentId, UUID assessmentId, List<UUID> answerIds) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with ID: " + studentId));
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));

        submission.setStudent(student);
        submission.setAssessment(assessment);
        submission.setSubmissionDate(LocalDateTime.now());

        // Automatic Grading Logic
        int score = 0;
        for (UUID answerId : answerIds) {
            Optional<Answer> answer = answerRepository.findById(answerId);
            if (answer.isPresent() && answer.get().getIsCorrect()) {
                score++;
            }
        }
        submission.setScore(score);

        return submissionRepository.save(submission);
    }

    public Submission updateSubmission(UUID id, Submission updatedSubmission, UUID assessmentId) {
         return submissionRepository.findById(id)
                .map(submission -> {
                    Assessment assessment = assessmentRepository.findById(assessmentId)
                            .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
                    submission.setAssessment(assessment);
                    submission.setFeedback(updatedSubmission.getFeedback());
                    return submissionRepository.save(submission);
                })
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + id));
    }

    public void deleteSubmission(UUID id) {
        submissionRepository.deleteById(id);
    }

    public List<Submission> getSubmissionsByStudentId(UUID studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public List<Submission> getSubmissionsByAssessmentId(UUID assessmentId) {
        return submissionRepository.findByAssessmentId(assessmentId);
    }
}