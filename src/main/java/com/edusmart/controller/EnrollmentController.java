package com.edusmart.controller;

import com.edusmart.dto.course.EnrollmentRequestDTO;
import com.edusmart.dto.course.EnrollmentResponseDTO;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for enrollment operations
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    /**
     * Enroll in a course (STUDENT only)
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponseDTO> enrollInCourse(
            @Valid @RequestBody EnrollmentRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        EnrollmentResponseDTO response = enrollmentService.enrollInCourse(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get my enrollments
     */
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(currentUser.getUsername(), pageable);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get enrollments by student ID
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentEnrollmentsById(@PathVariable Long studentId) {
        try {
            var enrollments = enrollmentService.getStudentEnrollmentsById(studentId);
            return ResponseEntity.ok(enrollments);
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }
    
    /**
     * Get enrollments for a course (INSTRUCTOR, ADMIN)
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getCourseEnrollments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EnrollmentResponseDTO> enrollments = enrollmentService.getCourseEnrollments(courseId, pageable);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get specific enrollment
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollment(@PathVariable Long id) {
        EnrollmentResponseDTO response = enrollmentService.getEnrollment(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update progress
     */
    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponseDTO> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        
        EnrollmentResponseDTO response = enrollmentService.updateProgress(id, progress);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if enrolled in course
     */
    @GetMapping("/check/{courseId}")
    public ResponseEntity<Map<String, Boolean>> checkEnrollment(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        boolean isEnrolled = enrollmentService.isEnrolled(currentUser.getUsername(), courseId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isEnrolled", isEnrolled);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Unenroll from course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> unenroll(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        enrollmentService.unenroll(id, currentUser.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Unenrolled successfully");
        return ResponseEntity.ok(response);
    }
}
