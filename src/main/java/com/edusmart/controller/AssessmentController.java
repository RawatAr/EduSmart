package com.edusmart.controller;

import com.edusmart.dto.assessment.*;
import com.edusmart.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for assessment and quiz management
 */
@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {
    
    private final AssessmentService assessmentService;
    
    /**
     * Create a new assessment
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AssessmentResponseDTO> createAssessment(
            @Valid @RequestBody AssessmentRequestDTO request,
            Authentication authentication) {
        AssessmentResponseDTO assessment = assessmentService.createAssessment(request, authentication.getName());
        return new ResponseEntity<>(assessment, HttpStatus.CREATED);
    }
    
    /**
     * Add question to assessment
     */
    @PostMapping("/{assessmentId}/questions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AssessmentResponseDTO> addQuestion(
            @PathVariable Long assessmentId,
            @Valid @RequestBody QuestionRequestDTO request,
            Authentication authentication) {
        AssessmentResponseDTO assessment = assessmentService.addQuestion(assessmentId, request, authentication.getName());
        return ResponseEntity.ok(assessment);
    }
    
    /**
     * Get assessment by ID
     */
    @GetMapping("/{assessmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AssessmentResponseDTO> getAssessment(
            @PathVariable Long assessmentId,
            Authentication authentication) {
        AssessmentResponseDTO assessment = assessmentService.getAssessment(assessmentId, authentication.getName());
        return ResponseEntity.ok(assessment);
    }
    
    /**
     * Get assessments for a course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AssessmentResponseDTO>> getCourseAssessments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AssessmentResponseDTO> assessments = assessmentService.getCourseAssessments(courseId, pageable);
        return ResponseEntity.ok(assessments);
    }
    
    /**
     * Submit assessment
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponseDTO> submitAssessment(
            @Valid @RequestBody SubmissionRequestDTO request,
            Authentication authentication) {
        SubmissionResponseDTO submission = assessmentService.submitAssessment(request, authentication.getName());
        return ResponseEntity.ok(submission);
    }
    
    /**
     * Get student's submissions for an assessment
     */
    @GetMapping("/{assessmentId}/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissions(
            @PathVariable Long assessmentId,
            Authentication authentication) {
        List<SubmissionResponseDTO> submissions = assessmentService.getStudentSubmissions(assessmentId, authentication.getName());
        return ResponseEntity.ok(submissions);
    }
    
    /**
     * Get all submissions for an assessment (instructor only)
     */
    @GetMapping("/{assessmentId}/submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Page<SubmissionResponseDTO>> getAssessmentSubmissions(
            @PathVariable Long assessmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponseDTO> submissions = assessmentService.getAssessmentSubmissions(
                assessmentId, pageable, authentication.getName());
        return ResponseEntity.ok(submissions);
    }
}
