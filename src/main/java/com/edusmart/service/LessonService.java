package com.edusmart.service;

import com.edusmart.dto.lesson.*;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.Role;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing lessons
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonService {
    
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Create a new lesson
     */
    public LessonResponseDTO createLesson(Long courseId, LessonRequestDTO request, String instructorUsername) {
        log.info("Creating lesson for course: {} by instructor: {}", courseId, instructorUsername);
        
        // Get course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        // Get instructor
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        // Check permissions
        if (!course.getInstructor().getId().equals(instructor.getId()) && 
            !instructor.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to add lessons to this course");
        }
        
        // Create lesson
        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .description(request.getDescription())
                .lessonType(request.getLessonType())
                .content(request.getContent())
                .videoUrl(request.getVideoUrl())
                .durationMinutes(request.getDurationMinutes())
                .lessonOrder(request.getLessonOrder())
                .isFree(request.getIsFree() != null ? request.getIsFree() : false)
                .attachmentUrl(request.getAttachmentUrl())
                .build();
        
        lesson = lessonRepository.save(lesson);
        log.info("Lesson created successfully: {}", lesson.getId());
        
        return mapToResponseDTO(lesson, null);
    }
    
    /**
     * Update a lesson
     */
    public LessonResponseDTO updateLesson(Long lessonId, LessonRequestDTO request, String username) {
        log.info("Updating lesson: {} by user: {}", lessonId, username);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!lesson.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to update this lesson");
        }
        
        // Update fields
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLessonType(request.getLessonType());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setLessonOrder(request.getLessonOrder());
        if (request.getIsFree() != null) {
            lesson.setIsFree(request.getIsFree());
        }
        lesson.setAttachmentUrl(request.getAttachmentUrl());
        
        lesson = lessonRepository.save(lesson);
        log.info("Lesson updated successfully: {}", lessonId);
        
        return mapToResponseDTO(lesson, null);
    }
    
    /**
     * Get lesson by ID
     */
    public LessonResponseDTO getLessonById(Long lessonId, String username) {
        log.info("Getting lesson: {} for user: {}", lessonId, username);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user has access (enrolled or instructor)
        boolean hasAccess = lesson.getCourse().getInstructor().getId().equals(user.getId()) ||
                           user.getRole().equals(Role.ADMIN) ||
                           enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), lesson.getCourse().getId());
        
        if (!hasAccess && !lesson.getIsFree()) {
            throw new BadRequestException("You don't have access to this lesson");
        }
        
        // Check if completed
        Optional<LessonCompletion> completion = lessonCompletionRepository
                .findByStudentIdAndLessonId(user.getId(), lessonId);
        
        return mapToResponseDTO(lesson, completion.orElse(null));
    }
    
    /**
     * Get all lessons for a course
     */
    public List<LessonListDTO> getCourseLessons(Long courseId, String username) {
        log.info("Getting lessons for course: {} for user: {}", courseId, username);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get lessons
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        
        // Check if user is enrolled
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
        boolean isInstructor = course.getInstructor().getId().equals(user.getId()) || 
                              user.getRole().equals(Role.ADMIN);
        
        // Filter lessons based on access
        if (!isEnrolled && !isInstructor) {
            lessons = lessons.stream()
                    .filter(Lesson::getIsFree)
                    .collect(Collectors.toList());
        }
        
        // Get completions for student
        List<LessonCompletion> completions = lessonCompletionRepository
                .findByStudentIdAndLessonCourseId(user.getId(), courseId);
        
        return lessons.stream()
                .map(lesson -> mapToListDTO(lesson, completions))
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a lesson
     */
    public void deleteLesson(Long lessonId, String username) {
        log.info("Deleting lesson: {} by user: {}", lessonId, username);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!lesson.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to delete this lesson");
        }
        
        lessonRepository.delete(lesson);
        log.info("Lesson deleted successfully: {}", lessonId);
    }
    
    /**
     * Mark lesson as completed
     */
    public LessonCompletionDTO markLessonCompleted(Long lessonId, String studentUsername) {
        log.info("Marking lesson {} as completed by student: {}", lessonId, studentUsername);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        // Check if student is enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), lesson.getCourse().getId())) {
            throw new BadRequestException("You must be enrolled in the course to complete lessons");
        }
        
        // Check if already completed
        Optional<LessonCompletion> existing = lessonCompletionRepository
                .findByStudentIdAndLessonId(student.getId(), lessonId);
        
        LessonCompletion completion;
        if (existing.isPresent()) {
            completion = existing.get();
            if (!completion.getCompleted()) {
                completion.setCompleted(true);
                completion.setCompletedAt(LocalDateTime.now());
                completion = lessonCompletionRepository.save(completion);
            }
        } else {
            completion = LessonCompletion.builder()
                    .lesson(lesson)
                    .student(student)
                    .completed(true)
                    .completedAt(LocalDateTime.now())
                    .build();
            completion = lessonCompletionRepository.save(completion);
        }
        
        log.info("Lesson completion recorded: {}", completion.getId());
        return mapToCompletionDTO(completion);
    }
    
    /**
     * Get lesson completion status
     */
    public List<LessonCompletionDTO> getStudentCompletions(Long courseId, String studentUsername) {
        log.info("Getting completions for course: {} and student: {}", courseId, studentUsername);
        
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        List<LessonCompletion> completions = lessonCompletionRepository
                .findByStudentIdAndLessonCourseId(student.getId(), courseId);
        
        return completions.stream()
                .map(this::mapToCompletionDTO)
                .collect(Collectors.toList());
    }
    
    // Mapping methods
    private LessonResponseDTO mapToResponseDTO(Lesson lesson, LessonCompletion completion) {
        return LessonResponseDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .lessonType(lesson.getLessonType())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .durationMinutes(lesson.getDurationMinutes())
                .lessonOrder(lesson.getLessonOrder())
                .isFree(lesson.getIsFree())
                .attachmentUrl(lesson.getAttachmentUrl())
                .courseId(lesson.getCourse().getId())
                .courseTitle(lesson.getCourse().getTitle())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .isCompleted(completion != null && completion.getCompleted())
                .completedAt(completion != null ? completion.getCompletedAt() : null)
                .build();
    }
    
    private LessonListDTO mapToListDTO(Lesson lesson, List<LessonCompletion> completions) {
        boolean isCompleted = completions.stream()
                .anyMatch(c -> c.getLesson().getId().equals(lesson.getId()) && c.getCompleted());
        
        return LessonListDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .lessonType(lesson.getLessonType())
                .durationMinutes(lesson.getDurationMinutes())
                .lessonOrder(lesson.getLessonOrder())
                .isFree(lesson.getIsFree())
                .isCompleted(isCompleted)
                .build();
    }
    
    private LessonCompletionDTO mapToCompletionDTO(LessonCompletion completion) {
        return LessonCompletionDTO.builder()
                .id(completion.getId())
                .lessonId(completion.getLesson().getId())
                .lessonTitle(completion.getLesson().getTitle())
                .studentId(completion.getStudent().getId())
                .studentName(completion.getStudent().getFirstName() + " " + completion.getStudent().getLastName())
                .completed(completion.getCompleted())
                .completedAt(completion.getCompletedAt())
                .createdAt(completion.getCreatedAt())
                .build();
    }
}
