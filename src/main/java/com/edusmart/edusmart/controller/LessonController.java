package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.model.Lesson;
import com.edusmart.edusmart.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<Lesson> getLessonById(@PathVariable UUID id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Lesson> createLesson(
            @RequestBody Lesson lesson,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(lessonService.createLesson(lesson, courseId));
    }

    @PutMapping("/{id}/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable UUID id,
            @RequestBody Lesson lesson,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lesson, courseId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<Lesson>> getLessonsByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }
}