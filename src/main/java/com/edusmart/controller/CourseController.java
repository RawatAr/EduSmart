package com.edusmart.controller;

import com.edusmart.dto.course.*;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.CourseService;
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
import java.util.List;
import java.util.Map;

/**
 * REST Controller for course operations
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * Create a new course (INSTRUCTOR, ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponseDTO> createCourse(
            @Valid @RequestBody CourseRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        CourseResponseDTO response = courseService.createCourse(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Update a course
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        CourseResponseDTO response = courseService.updateCourse(id, request, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO response = courseService.getCourseById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all courses with pagination
     */
    @GetMapping
    public ResponseEntity<Page<CourseListDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<CourseListDTO> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Search courses
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CourseListDTO>> searchCourses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseListDTO> courses = courseService.searchCourses(keyword, pageable);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Get courses by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<CourseListDTO>> getCoursesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseListDTO> courses = courseService.getCoursesByCategory(categoryId, pageable);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Get courses by instructor
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<Page<CourseListDTO>> getCoursesByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseListDTO> courses = courseService.getCoursesByInstructor(instructorId, pageable);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Get featured courses
     */
    @GetMapping("/featured")
    public ResponseEntity<List<CourseListDTO>> getFeaturedCourses() {
        List<CourseListDTO> courses = courseService.getFeaturedCourses();
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Publish/Unpublish course
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponseDTO> togglePublishStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        CourseResponseDTO response = courseService.togglePublishStatus(id, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete course
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        courseService.deleteCourse(id, currentUser.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Course deleted successfully");
        return ResponseEntity.ok(response);
    }
}
