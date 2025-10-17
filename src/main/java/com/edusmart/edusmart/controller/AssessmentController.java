package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.Assessment;
import com.edusmart.edusmart.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable UUID id) {
        return assessmentService.getAssessmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Assessment> createAssessment(
            @RequestBody Assessment assessment,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(assessmentService.createAssessment(assessment, courseId));
    }

    @PutMapping("/{id}/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Assessment> updateAssessment(
            @PathVariable UUID id,
            @RequestBody Assessment assessment,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, assessment, courseId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable UUID id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<Assessment>> getAssessmentsByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourseId(courseId));
    }
}