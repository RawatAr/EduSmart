package com.edusmart.edusmart.controller.v1;

import com.edusmart.edusmart.dto.CourseDTO;
import com.edusmart.edusmart.dto.CourseSearchDTO;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseControllerV1 {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Get all courses", description = "Retrieve a paginated list of all courses")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @ModelAttribute CourseSearchDTO searchDTO) {
        Page<CourseDTO> courses = courseService.searchCourses(searchDTO);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable UUID id) {
        return courseService.getCourseById(id)
                .map(courseService::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create a new course", description = "Create a new course (Instructor/Admin only)")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        Course course = courseService.convertToEntity(courseDTO);
        Course savedCourse = courseService.createCourse(course, courseDTO.getInstructorId(), courseDTO.getCategoryId());
        CourseDTO responseDTO = courseService.convertToDTO(savedCourse);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update a course", description = "Update an existing course")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseDTO courseDTO) {
        Course course = courseService.convertToEntity(courseDTO);
        Course updatedCourse = courseService.updateCourse(id, course, courseDTO.getCategoryId());
        CourseDTO responseDTO = courseService.convertToDTO(updatedCourse);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete a course", description = "Delete a course by its ID")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Get courses by category", description = "Retrieve courses belonging to a specific category")
    public ResponseEntity<List<CourseDTO>> getCoursesByCategoryId(@PathVariable UUID categoryId) {
        List<Course> courses = courseService.getCoursesByCategoryId(categoryId);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(courseService::convertToDTO)
                .toList();
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Get courses by instructor", description = "Retrieve courses created by a specific instructor")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructorId(@PathVariable UUID instructorId) {
        List<Course> courses = courseService.getCoursesByInstructorId(instructorId);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(courseService::convertToDTO)
                .toList();
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Search courses", description = "Search courses with advanced filtering and pagination")
    public ResponseEntity<Page<CourseDTO>> searchCourses(@ModelAttribute CourseSearchDTO searchDTO) {
        Page<CourseDTO> courses = courseService.searchCourses(searchDTO);
        return ResponseEntity.ok(courses);
    }
}