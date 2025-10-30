package com.edusmart.service;

import com.edusmart.dto.assessment.*;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.QuestionType;
import com.edusmart.entity.enums.Role;
import com.edusmart.entity.enums.SubmissionStatus;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing assessments, quizzes, and submissions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Create a new assessment
     */
    public AssessmentResponseDTO createAssessment(AssessmentRequestDTO request, String instructorUsername) {
        log.info("Creating assessment: {} by instructor: {}", request.getTitle(), instructorUsername);
        
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        // Check permissions
        if (!course.getInstructor().getId().equals(instructor.getId()) && 
            !instructor.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to create assessments for this course");
        }
        
        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assessmentType(request.getAssessmentType())
                .course(course)
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .maxAttempts(request.getMaxAttempts())
                .showResultsImmediately(request.getShowResultsImmediately())
                .build();
        
        // Link to lesson if provided
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
            assessment.setLesson(lesson);
        }
        
        assessment = assessmentRepository.save(assessment);
        log.info("Assessment created: {}", assessment.getId());
        
        return mapToResponseDTO(assessment, false);
    }
    
    /**
     * Add question to assessment
     */
    public AssessmentResponseDTO addQuestion(Long assessmentId, QuestionRequestDTO request, String username) {
        log.info("Adding question to assessment: {}", assessmentId);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!assessment.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to add questions");
        }
        
        Question question = Question.builder()
                .assessment(assessment)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .marks(request.getMarks())
                .orderNumber(request.getOrderNumber())
                .explanation(request.getExplanation())
                .modelAnswer(request.getModelAnswer())
                .build();
        
        question = questionRepository.save(question);
        
        // Add options if MCQ or True/False
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (QuestionOptionRequestDTO optionDTO : request.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(optionDTO.getOptionText())
                        .isCorrect(optionDTO.getIsCorrect() != null ? optionDTO.getIsCorrect() : false)
                        .orderNumber(optionDTO.getOrderNumber())
                        .build();
                questionOptionRepository.save(option);
            }
        }
        
        log.info("Question added: {}", question.getId());
        return mapToResponseDTO(assessment, true);
    }
    
    /**
     * Get assessment by ID
     */
    public AssessmentResponseDTO getAssessment(Long assessmentId, String username) {
        log.info("Getting assessment: {} for user: {}", assessmentId, username);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        boolean isInstructor = assessment.getCourse().getInstructor().getId().equals(user.getId()) ||
                              user.getRole().equals(Role.ADMIN);
        
        return mapToResponseDTO(assessment, isInstructor);
    }
    
    /**
     * Get assessments for a course
     */
    public Page<AssessmentResponseDTO> getCourseAssessments(Long courseId, Pageable pageable) {
        log.info("Getting assessments for course: {}", courseId);
        
        Page<Assessment> assessments = assessmentRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable);
        return assessments.map(a -> mapToResponseDTO(a, false));
    }
    
    /**
     * Submit assessment
     */
    public SubmissionResponseDTO submitAssessment(SubmissionRequestDTO request, String studentUsername) {
        log.info("Submitting assessment: {} by student: {}", request.getAssessmentId(), studentUsername);
        
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        // Check if student is enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), assessment.getCourse().getId())) {
            throw new BadRequestException("You must be enrolled in the course to submit this assessment");
        }
        
        // Check if already submitted max attempts
        long attemptCount = submissionRepository.countByAssessmentIdAndStudentId(
                assessment.getId(), student.getId());
        if (assessment.getMaxAttempts() != null && attemptCount >= assessment.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempts reached for this assessment");
        }
        
        // Create submission
        Submission submission = Submission.builder()
                .assessment(assessment)
                .student(student)
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.SUBMITTED)
                .build();
        
        submission = submissionRepository.save(submission);
        
        // Process answers and auto-grade
        int totalObtained = 0;
        List<SubmissionResponseDTO.AnswerResultDTO> results = new ArrayList<>();
        
        for (SubmissionRequestDTO.AnswerDTO answerDTO : request.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
            
            // Auto-grade MCQ and True/False
            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE || 
                question.getQuestionType() == QuestionType.TRUE_FALSE) {
                
                QuestionOption selectedOption = null;
                if (answerDTO.getSelectedOptionId() != null) {
                    selectedOption = questionOptionRepository.findById(answerDTO.getSelectedOptionId())
                            .orElse(null);
                }
                
                int marksObtained = 0;
                boolean isCorrect = false;
                if (selectedOption != null && selectedOption.getIsCorrect()) {
                    marksObtained = question.getMarks();
                    isCorrect = true;
                }
                totalObtained += marksObtained;
                
                results.add(SubmissionResponseDTO.AnswerResultDTO.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .studentAnswer(selectedOption != null ? selectedOption.getOptionText() : "No answer")
                        .isCorrect(isCorrect)
                        .marksObtained(marksObtained)
                        .maxMarks(question.getMarks())
                        .build());
            } else {
                // Subjective - needs manual grading
                results.add(SubmissionResponseDTO.AnswerResultDTO.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .studentAnswer(answerDTO.getAnswerText())
                        .marksObtained(0)
                        .maxMarks(question.getMarks())
                        .build());
            }
        }
        
        // Update submission
        submission.setObtainedMarks(totalObtained);
        
        // Check if all questions are auto-graded
        boolean allAutoGraded = assessment.getQuestions().stream()
                .allMatch(q -> q.getQuestionType() == QuestionType.MULTIPLE_CHOICE || 
                              q.getQuestionType() == QuestionType.TRUE_FALSE);
        
        if (allAutoGraded) {
            submission.setStatus(SubmissionStatus.GRADED);
            submission.setGradedAt(LocalDateTime.now());
        } else {
            submission.setStatus(SubmissionStatus.PENDING_REVIEW);
        }
        
        submission = submissionRepository.save(submission);
        log.info("Submission completed: {}", submission.getId());
        
        return mapToSubmissionDTO(submission, results, assessment.getShowResultsImmediately());
    }
    
    /**
     * Get student submissions for an assessment
     */
    public List<SubmissionResponseDTO> getStudentSubmissions(Long assessmentId, String studentUsername) {
        log.info("Getting submissions for assessment: {} by student: {}", assessmentId, studentUsername);
        
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        List<Submission> submissions = submissionRepository
                .findByAssessmentIdAndStudentIdOrderBySubmittedAtDesc(assessmentId, student.getId());
        
        return submissions.stream()
                .map(s -> mapToSubmissionDTO(s, null, true))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all submissions for an assessment (instructor only)
     */
    public Page<SubmissionResponseDTO> getAssessmentSubmissions(Long assessmentId, Pageable pageable, String username) {
        log.info("Getting all submissions for assessment: {}", assessmentId);
        
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!assessment.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to view these submissions");
        }
        
        Page<Submission> submissions = submissionRepository
                .findByAssessmentIdOrderBySubmittedAtDesc(assessmentId, pageable);
        
        return submissions.map(s -> mapToSubmissionDTO(s, null, true));
    }
    
    // Mapping methods
    private AssessmentResponseDTO mapToResponseDTO(Assessment assessment, boolean includeQuestions) {
        AssessmentResponseDTO.AssessmentResponseDTOBuilder builder = AssessmentResponseDTO.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .assessmentType(assessment.getAssessmentType())
                .courseId(assessment.getCourse().getId())
                .courseTitle(assessment.getCourse().getTitle())
                .durationMinutes(assessment.getDurationMinutes())
                .totalMarks(assessment.getTotalMarks())
                .passingMarks(assessment.getPassingMarks())
                .startTime(assessment.getStartTime())
                .endTime(assessment.getEndTime())
                .isPublished(assessment.getIsPublished())
                .maxAttempts(assessment.getMaxAttempts())
                .showResultsImmediately(assessment.getShowResultsImmediately())
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt());
        
        if (assessment.getLesson() != null) {
            builder.lessonId(assessment.getLesson().getId())
                   .lessonTitle(assessment.getLesson().getTitle());
        }
        
        if (includeQuestions && assessment.getQuestions() != null) {
            List<QuestionResponseDTO> questions = assessment.getQuestions().stream()
                    .map(this::mapToQuestionDTO)
                    .collect(Collectors.toList());
            builder.questions(questions);
        }
        
        return builder.build();
    }
    
    private QuestionResponseDTO mapToQuestionDTO(Question question) {
        List<QuestionOptionResponseDTO> options = null;
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            options = question.getOptions().stream()
                    .map(o -> QuestionOptionResponseDTO.builder()
                            .id(o.getId())
                            .optionText(o.getOptionText())
                            .isCorrect(o.getIsCorrect())
                            .orderNumber(o.getOrderNumber())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .marks(question.getMarks())
                .orderNumber(question.getOrderNumber())
                .explanation(question.getExplanation())
                .options(options)
                .modelAnswer(question.getModelAnswer())
                .build();
    }
    
    private SubmissionResponseDTO mapToSubmissionDTO(Submission submission, 
                                                     List<SubmissionResponseDTO.AnswerResultDTO> answers,
                                                     boolean showResults) {
        double percentage = submission.getObtainedMarks() != null && submission.getAssessment().getTotalMarks() != null
                ? (submission.getObtainedMarks() * 100.0) / submission.getAssessment().getTotalMarks()
                : 0.0;
        
        boolean passed = submission.getAssessment().getPassingMarks() != null && 
                        submission.getObtainedMarks() != null &&
                        submission.getObtainedMarks() >= submission.getAssessment().getPassingMarks();
        
        return SubmissionResponseDTO.builder()
                .id(submission.getId())
                .assessmentId(submission.getAssessment().getId())
                .assessmentTitle(submission.getAssessment().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName())
                .status(submission.getStatus())
                .obtainedMarks(submission.getObtainedMarks())
                .totalMarks(submission.getAssessment().getTotalMarks())
                .percentage(percentage)
                .passed(passed)
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .answers(showResults ? answers : null)
                .feedback(submission.getFeedback())
                .build();
    }
}
