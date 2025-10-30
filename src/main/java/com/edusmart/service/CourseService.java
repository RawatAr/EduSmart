package com.edusmart.service;

import com.edusmart.dto.course.*;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.Role;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing courses
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    
    /**
     * Create a new course
     */
    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponseDTO createCourse(CourseRequestDTO request, String instructorUsername) {
        log.info("Creating course: {} by instructor: {}", request.getTitle(), instructorUsername);
        
        // Get instructor
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        if (!instructor.getRole().equals(Role.INSTRUCTOR) && !instructor.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("Only instructors can create courses");
        }
        
        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        // Create course
        Course course = Course.builder()
                .title(request.getTitle())
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .videoPreviewUrl(request.getVideoPreviewUrl())
                .level(request.getLevel())
                .price(request.getPrice())
                .durationHours(request.getDurationHours())
                .language(request.getLanguage() != null ? request.getLanguage() : "English")
                .requirements(request.getRequirements())
                .targetAudience(request.getTargetAudience())
                .learningObjectives(request.getLearningObjectives())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .isFeatured(false)
                .category(category)
                .instructor(instructor)
                .build();
        
        course = courseRepository.save(course);
        log.info("Course created successfully with id: {}", course.getId());
        
        return mapToResponseDTO(course);
    }
    
    /**
     * Update existing course
     */
    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO request, String username) {
        log.info("Updating course: {} by user: {}", courseId, username);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!course.getInstructor().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to update this course");
        }
        
        // Update category if changed
        if (request.getCategoryId() != null && !request.getCategoryId().equals(course.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            course.setCategory(category);
        }
        
        // Update fields
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getShortDescription() != null) course.setShortDescription(request.getShortDescription());
        if (request.getFullDescription() != null) course.setFullDescription(request.getFullDescription());
        if (request.getThumbnailUrl() != null) course.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getVideoPreviewUrl() != null) course.setVideoPreviewUrl(request.getVideoPreviewUrl());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getDurationHours() != null) course.setDurationHours(request.getDurationHours());
        if (request.getLanguage() != null) course.setLanguage(request.getLanguage());
        if (request.getRequirements() != null) course.setRequirements(request.getRequirements());
        if (request.getTargetAudience() != null) course.setTargetAudience(request.getTargetAudience());
        if (request.getLearningObjectives() != null) course.setLearningObjectives(request.getLearningObjectives());
        if (request.getIsPublished() != null) course.setIsPublished(request.getIsPublished());
        
        course = courseRepository.save(course);
        log.info("Course updated successfully: {}", courseId);
        
        return mapToResponseDTO(course);
    }
    
    /**
     * Get course by ID
     */
    @Cacheable(value = "courses", key = "'course_' + #courseId")
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long courseId) {
        log.info("Fetching course {} from database (not cached)", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return mapToResponseDTO(course);
    }
    
    /**
     * Get all courses with pagination
     */
    @Cacheable(value = "courses", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<CourseListDTO> getAllCourses(Pageable pageable) {
        log.info("Fetching all courses from database (not cached)");
        Page<Course> courses = courseRepository.findByIsPublished(true, pageable);
        return courses.map(this::mapToListDTO);
    }
    
    /**
     * Get courses by category
     */
    @Cacheable(value = "courses", key = "'category_' + #categoryId + '_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<CourseListDTO> getCoursesByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching courses for category {} from database (not cached)", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        Page<Course> courses = courseRepository.findByCategoryAndIsPublished(category, true, pageable);
        return courses.map(this::mapToListDTO);
    }
    
    /**
     * Get courses by instructor
     */
    @Transactional(readOnly = true)
    public Page<CourseListDTO> getCoursesByInstructor(Long instructorId, Pageable pageable) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        Page<Course> courses = courseRepository.findByInstructor(instructor, pageable);
        return courses.map(this::mapToListDTO);
    }
    
    /**
     * Search courses by title or description
     */
    @Transactional(readOnly = true)
    public Page<CourseListDTO> searchCourses(String keyword, Pageable pageable) {
        Page<Course> courses = courseRepository.findByTitleContainingIgnoreCaseOrShortDescriptionContainingIgnoreCaseAndIsPublished(
                keyword, keyword, true, pageable);
        return courses.map(this::mapToListDTO);
    }
    
    /**
     * Get featured courses
     */
    @Transactional(readOnly = true)
    public List<CourseListDTO> getFeaturedCourses() {
        List<Course> courses = courseRepository.findByIsFeaturedAndIsPublished(true, true);
        return courses.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete course
     */
    public void deleteCourse(Long courseId, String username) {
        log.info("Deleting course: {} by user: {}", courseId, username);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!course.getInstructor().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to delete this course");
        }
        
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", courseId);
    }
    
    /**
     * Publish/Unpublish course
     */
    public CourseResponseDTO togglePublishStatus(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!course.getInstructor().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to publish/unpublish this course");
        }
        
        course.setIsPublished(!course.getIsPublished());
        course = courseRepository.save(course);
        
        log.info("Course {} status changed to: {}", courseId, course.getIsPublished() ? "Published" : "Unpublished");
        return mapToResponseDTO(course);
    }
    
    /**
     * Map Course entity to CourseResponseDTO
     */
    private CourseResponseDTO mapToResponseDTO(Course course) {
        int enrollmentCount = enrollmentRepository.countByCourse(course);
        int lessonCount = lessonRepository.countByCourse(course);
        
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .shortDescription(course.getShortDescription())
                .fullDescription(course.getFullDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .videoPreviewUrl(course.getVideoPreviewUrl())
                .level(course.getLevel())
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .language(course.getLanguage())
                .isPublished(course.getIsPublished())
                .isFeatured(course.getIsFeatured())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName())
                .instructorEmail(course.getInstructor().getEmail())
                .enrollmentCount(enrollmentCount)
                .lessonCount(lessonCount)
                .averageRating(0.0) // TODO: Implement rating system
                .reviewCount(0)
                .requirements(course.getRequirements())
                .targetAudience(course.getTargetAudience())
                .learningObjectives(course.getLearningObjectives())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
    
    /**
     * Map Course entity to CourseListDTO
     */
    private CourseListDTO mapToListDTO(Course course) {
        int enrollmentCount = enrollmentRepository.countByCourse(course);
        int lessonCount = lessonRepository.countByCourse(course);
        
        return CourseListDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .shortDescription(course.getShortDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .level(course.getLevel())
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .language(course.getLanguage())
                .isPublished(course.getIsPublished())
                .isFeatured(course.getIsFeatured())
                .categoryName(course.getCategory().getName())
                .instructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName())
                .enrollmentCount(enrollmentCount)
                .lessonCount(lessonCount)
                .averageRating(0.0)
                .reviewCount(0)
                .build();
    }
}
