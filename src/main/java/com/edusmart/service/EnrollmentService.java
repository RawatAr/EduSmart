package com.edusmart.service;

import com.edusmart.dto.course.EnrollmentRequestDTO;
import com.edusmart.dto.course.EnrollmentResponseDTO;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.EnrollmentStatus;
import com.edusmart.entity.enums.Role;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing course enrollments
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    /**
     * Enroll student in a course
     */
    public EnrollmentResponseDTO enrollInCourse(EnrollmentRequestDTO request, String studentUsername) {
        log.info("Enrolling student: {} in course: {}", studentUsername, request.getCourseId());
        
        // Get student
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new BadRequestException("Only students can enroll in courses");
        }
        
        // Get course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        if (!course.getIsPublished()) {
            throw new BadRequestException("Course is not published yet");
        }
        
        // Check if already enrolled
        if (enrollmentRepository.findByStudentAndCourse(student, course).isPresent()) {
            throw new BadRequestException("You are already enrolled in this course");
        }
        
        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progress(0)
                .isCompleted(false)
                .completed(false)
                .enrollmentDate(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .build();
        
        enrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully with id: {}", enrollment.getId());
        
        return mapToResponseDTO(enrollment);
    }
    
    /**
     * Get student's enrollments
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> getStudentEnrollments(String studentUsername, Pageable pageable) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Page<Enrollment> enrollments = enrollmentRepository.findByStudent(student, pageable);
        return enrollments.map(this::mapToResponseDTO);
    }
    
    /**
     * Get student's enrollments by ID
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getStudentEnrollmentsById(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        return enrollments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get enrollments for a course
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> getCourseEnrollments(Long courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        Page<Enrollment> enrollments = enrollmentRepository.findByCourse(course, pageable);
        return enrollments.map(this::mapToResponseDTO);
    }
    
    /**
     * Get specific enrollment
     */
    @Transactional(readOnly = true)
    public EnrollmentResponseDTO getEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        return mapToResponseDTO(enrollment);
    }
    
    /**
     * Update enrollment progress
     */
    public EnrollmentResponseDTO updateProgress(Long enrollmentId, Integer progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        if (progress < 0 || progress > 100) {
            throw new BadRequestException("Progress must be between 0 and 100");
        }
        
        enrollment.setProgress(progress);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        
        if (progress == 100 && !enrollment.getIsCompleted()) {
            enrollment.setIsCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
            log.info("Student {} completed course {}", enrollment.getStudent().getUsername(), enrollment.getCourse().getTitle());
        }
        
        enrollment = enrollmentRepository.save(enrollment);
        return mapToResponseDTO(enrollment);
    }
    
    /**
     * Unenroll from course
     */
    public void unenroll(Long enrollmentId, String username) {
        log.info("Unenrolling user: {} from enrollment: {}", username, enrollmentId);
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check permissions
        if (!enrollment.getStudent().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to unenroll from this course");
        }
        
        enrollmentRepository.delete(enrollment);
        log.info("Unenrollment successful");
    }
    
    /**
     * Check if user is enrolled in course
     */
    @Transactional(readOnly = true)
    public boolean isEnrolled(String username, Long courseId) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        return enrollmentRepository.findByStudentAndCourse(student, course).isPresent();
    }
    
    /**
     * Map Enrollment entity to EnrollmentResponseDTO
     */
    private EnrollmentResponseDTO mapToResponseDTO(Enrollment enrollment) {
        // Create course map with details
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("id", enrollment.getCourse().getId());
        courseMap.put("title", enrollment.getCourse().getTitle());
        courseMap.put("thumbnailUrl", enrollment.getCourse().getThumbnailUrl());
        courseMap.put("description", enrollment.getCourse().getDescription());
        courseMap.put("level", enrollment.getCourse().getLevel());
        courseMap.put("price", enrollment.getCourse().getPrice());
        
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnail(enrollment.getCourse().getThumbnailUrl())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName())
                .status(enrollment.getStatus())
                .progress(enrollment.getProgress())
                .isCompleted(enrollment.getIsCompleted())
                .enrolledAt(enrollment.getCreatedAt())
                .completedAt(enrollment.getCompletedAt())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .course(courseMap)
                .build();
    }
}
