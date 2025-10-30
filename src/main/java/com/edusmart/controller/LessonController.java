package com.edusmart.controller;

import com.edusmart.dto.lesson.*;
import com.edusmart.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for lesson management
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    
    private final LessonService lessonService;
    
    /**
     * Create a new lesson for a course
     */
    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResponseDTO> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonRequestDTO request,
            Authentication authentication) {
        LessonResponseDTO lesson = lessonService.createLesson(courseId, request, authentication.getName());
        return new ResponseEntity<>(lesson, HttpStatus.CREATED);
    }
    
    /**
     * Update a lesson
     */
    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResponseDTO> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequestDTO request,
            Authentication authentication) {
        LessonResponseDTO lesson = lessonService.updateLesson(lessonId, request, authentication.getName());
        return ResponseEntity.ok(lesson);
    }
    
    /**
     * Get lesson by ID
     */
    @GetMapping("/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LessonResponseDTO> getLessonById(
            @PathVariable Long lessonId,
            Authentication authentication) {
        LessonResponseDTO lesson = lessonService.getLessonById(lessonId, authentication.getName());
        return ResponseEntity.ok(lesson);
    }
    
    /**
     * Get all lessons for a course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LessonListDTO>> getCourseLessons(
            @PathVariable Long courseId,
            Authentication authentication) {
        List<LessonListDTO> lessons = lessonService.getCourseLessons(courseId, authentication.getName());
        return ResponseEntity.ok(lessons);
    }
    
    /**
     * Delete a lesson
     */
    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long lessonId,
            Authentication authentication) {
        lessonService.deleteLesson(lessonId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Mark lesson as completed
     */
    @PostMapping("/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonCompletionDTO> markLessonCompleted(
            @PathVariable Long lessonId,
            Authentication authentication) {
        LessonCompletionDTO completion = lessonService.markLessonCompleted(lessonId, authentication.getName());
        return ResponseEntity.ok(completion);
    }
    
    /**
     * Get student's lesson completions for a course
     */
    @GetMapping("/course/{courseId}/completions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LessonCompletionDTO>> getStudentCompletions(
            @PathVariable Long courseId,
            Authentication authentication) {
        List<LessonCompletionDTO> completions = lessonService.getStudentCompletions(courseId, authentication.getName());
        return ResponseEntity.ok(completions);
    }
}
