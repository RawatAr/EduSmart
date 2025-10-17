package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.Submission;
import com.edusmart.edusmart.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable UUID id) {
        return submissionService.getSubmissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/student/{studentId}/assessment/{assessmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Submission> createSubmission(
            @RequestBody Submission submission,
            @PathVariable UUID studentId,
            @PathVariable UUID assessmentId,
            @RequestParam List<UUID> answerIds
    ) {
        return ResponseEntity.ok(submissionService.createSubmission(submission, studentId, assessmentId, answerIds));
    }

    @PutMapping("/{id}/assessment/{assessmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Submission> updateSubmission(
            @PathVariable UUID id,
            @RequestBody Submission submission,
            @PathVariable UUID assessmentId
    ) {
        return ResponseEntity.ok(submissionService.updateSubmission(id, submission, assessmentId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteSubmission(@PathVariable UUID id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Submission>> getSubmissionsByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByStudentId(studentId));
    }

    @GetMapping("/assessment/{assessmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Submission>> getSubmissionsByAssessmentId(@PathVariable UUID assessmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssessmentId(assessmentId));
    }
}