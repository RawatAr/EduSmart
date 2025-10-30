package com.edusmart.service;

import com.edusmart.dto.assessment.AssessmentSubmissionDTO;
import com.edusmart.dto.assessment.GradeResultDTO;
import com.edusmart.dto.assessment.QuestionAnswerDTO;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.AssessmentType;
import com.edusmart.entity.enums.NotificationType;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for automatic grading of assessments
 * Handles quiz auto-grading and assignment evaluation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradingService {
    
    private final AssessmentRepository assessmentRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final NotificationService notificationService;
    
    /**
     * Auto-grade a quiz submission
     */
    public GradeResultDTO autoGradeQuiz(Long assessmentId, Long studentId, AssessmentSubmissionDTO submissionDTO) {
        log.info("Auto-grading quiz {} for student {}", assessmentId, studentId);
        
        // Validate assessment exists and is a quiz
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        if (assessment.getAssessmentType() != AssessmentType.QUIZ) {
            throw new IllegalArgumentException("Auto-grading only available for quizzes");
        }
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        // Grade the submission
        GradeResult gradeResult = calculateGrade(assessment, submissionDTO);
        
        // Save grade to database
        Grade grade = Grade.builder()
                .assessment(assessment)
                .student(student)
                .score(gradeResult.getScore())
                .maxScore(gradeResult.getMaxScore())
                .percentage(gradeResult.getPercentage())
                .feedback(gradeResult.getFeedback())
                .gradedAt(LocalDateTime.now())
                .build();
        
        grade = gradeRepository.save(grade);
        
        // Send notification to student
        notificationService.createNotification(
            studentId,
            "Quiz Graded",
            String.format("Your quiz '%s' has been graded. Score: %.2f%%", 
                assessment.getTitle(), gradeResult.getPercentage()),
            NotificationType.GRADE,
            "/quizzes"
        );
        
        log.info("Quiz graded successfully. Score: {}/{} ({}%)", 
            gradeResult.getScore(), gradeResult.getMaxScore(), gradeResult.getPercentage());
        
        return GradeResultDTO.builder()
                .gradeId(grade.getId())
                .assessmentId(assessmentId)
                .studentId(studentId)
                .score(gradeResult.getScore())
                .maxScore(gradeResult.getMaxScore())
                .percentage(gradeResult.getPercentage())
                .feedback(gradeResult.getFeedback())
                .correctAnswers(gradeResult.getCorrectAnswers())
                .incorrectAnswers(gradeResult.getIncorrectAnswers())
                .passed(assessment.getPassingScore() != null && gradeResult.getPercentage() >= assessment.getPassingScore().doubleValue())
                .gradedAt(grade.getGradedAt())
                .build();
    }
    
    /**
     * Calculate grade based on answers
     */
    private GradeResult calculateGrade(Assessment assessment, AssessmentSubmissionDTO submission) {
        List<QuestionAnswerDTO> answers = submission.getAnswers();
        
        int totalQuestions = answers.size();
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        double totalScore = 0;
        double maxScore = assessment.getTotalPoints() != null ? assessment.getTotalPoints().doubleValue() : 100.0;
        
        List<String> feedbackList = new ArrayList<>();
        
        for (QuestionAnswerDTO answer : answers) {
            if (validateAnswer(answer)) {
                correctAnswers++;
                double questionPoints = maxScore / totalQuestions;
                totalScore += questionPoints;
                feedbackList.add("Question " + answer.getQuestionNumber() + ": Correct ✓");
            } else {
                incorrectAnswers++;
                feedbackList.add("Question " + answer.getQuestionNumber() + ": Incorrect ✗ (Correct answer: " + answer.getCorrectAnswer() + ")");
            }
        }
        
        double percentage = totalQuestions > 0 ? (totalScore / maxScore) * 100 : 0;
        String feedback = String.join("\n", feedbackList);
        
        return new GradeResult(totalScore, maxScore, percentage, correctAnswers, incorrectAnswers, feedback);
    }
    
    /**
     * Validate student answer against correct answer
     */
    private boolean validateAnswer(QuestionAnswerDTO answer) {
        if (answer.getStudentAnswer() == null || answer.getCorrectAnswer() == null) {
            return false;
        }
        
        // Trim and compare case-insensitively
        String studentAnswer = answer.getStudentAnswer().trim().toLowerCase();
        String correctAnswer = answer.getCorrectAnswer().trim().toLowerCase();
        
        // For multiple choice, exact match required
        if (answer.getQuestionType() != null && answer.getQuestionType().equals("MULTIPLE_CHOICE")) {
            return studentAnswer.equals(correctAnswer);
        }
        
        // For text answers, allow partial match or synonyms
        if (answer.getQuestionType() != null && answer.getQuestionType().equals("SHORT_ANSWER")) {
            // Check if student answer contains correct answer or vice versa
            return studentAnswer.contains(correctAnswer) || correctAnswer.contains(studentAnswer);
        }
        
        // Default: exact match
        return studentAnswer.equals(correctAnswer);
    }
    
    /**
     * Grade an assignment (manual grading with AI assist placeholder)
     */
    public void submitAssignmentForGrading(Long assessmentId, Long studentId, AssessmentSubmissionDTO submissionDTO) {
        log.info("Submitting assignment {} for grading by student {}", assessmentId, studentId);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        // Create pending grade record
        Grade grade = Grade.builder()
                .assessment(assessment)
                .student(student)
                .score(0.0)
                .maxScore(assessment.getTotalPoints() != null ? assessment.getTotalPoints().doubleValue() : 100.0)
                .percentage(0.0)
                .feedback("Assignment submitted. Awaiting instructor review.")
                .gradedAt(null) // Not graded yet
                .build();
        
        gradeRepository.save(grade);
        
        // Notify student of submission
        notificationService.createNotification(
            studentId,
            "Assignment Submitted",
            String.format("Your assignment '%s' has been submitted successfully.", assessment.getTitle()),
            NotificationType.ASSIGNMENT,
            "/assignments"
        );
        
        // Notify instructor (Course instructor)
        if (assessment.getCourse() != null && assessment.getCourse().getInstructor() != null) {
            notificationService.createNotification(
                assessment.getCourse().getInstructor().getId(),
                "New Assignment Submission",
                String.format("Student %s has submitted assignment '%s'.", 
                    student.getFirstName() + " " + student.getLastName(), 
                    assessment.getTitle()),
                NotificationType.ASSIGNMENT,
                "/instructor/assignments"
            );
        }
    }
    
    /**
     * Get grade for student's assessment
     */
    public GradeResultDTO getGrade(Long assessmentId, Long studentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Grade grade = gradeRepository.findByAssessmentAndStudent(assessment, student)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        
        return GradeResultDTO.builder()
                .gradeId(grade.getId())
                .assessmentId(assessmentId)
                .studentId(studentId)
                .score(grade.getScore())
                .maxScore(grade.getMaxScore())
                .percentage(grade.getPercentage())
                .feedback(grade.getFeedback())
                .passed(assessment.getPassingScore() != null && grade.getPercentage() >= assessment.getPassingScore().doubleValue())
                .gradedAt(grade.getGradedAt())
                .build();
    }
    
    // Inner class for grade calculation result
    private static class GradeResult {
        private final double score;
        private final double maxScore;
        private final double percentage;
        private final int correctAnswers;
        private final int incorrectAnswers;
        private final String feedback;
        
        public GradeResult(double score, double maxScore, double percentage, 
                          int correctAnswers, int incorrectAnswers, String feedback) {
            this.score = score;
            this.maxScore = maxScore;
            this.percentage = percentage;
            this.correctAnswers = correctAnswers;
            this.incorrectAnswers = incorrectAnswers;
            this.feedback = feedback;
        }
        
        public double getScore() { return score; }
        public double getMaxScore() { return maxScore; }
        public double getPercentage() { return percentage; }
        public int getCorrectAnswers() { return correctAnswers; }
        public int getIncorrectAnswers() { return incorrectAnswers; }
        public String getFeedback() { return feedback; }
    }
}
