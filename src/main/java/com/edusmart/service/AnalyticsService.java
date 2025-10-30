package com.edusmart.service;

import com.edusmart.dto.analytics.*;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.EnrollmentStatus;
import com.edusmart.entity.enums.Role;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for analytics and reporting
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final AssessmentRepository assessmentRepository;
    private final SubmissionRepository submissionRepository;
    private final DiscussionRepository discussionRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationRepository notificationRepository;
    
    /**
     * Get course analytics
     */
    public CourseAnalyticsDTO getCourseAnalytics(Long courseId, String username) {
        log.info("Getting analytics for course: {}", courseId);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user is instructor of this course or admin
        if (!courseRepository.existsByIdAndInstructorId(courseId, user.getId()) 
            && !user.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("You don't have permission to view these analytics");
        }
        
        Long totalEnrollments = enrollmentRepository.countByCourseId(courseId);
        Long activeEnrollments = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
        Long completedEnrollments = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.COMPLETED);
        
        Double completionRate = totalEnrollments > 0 
            ? (completedEnrollments * 100.0) / totalEnrollments 
            : 0.0;
        
        Long totalLessons = lessonRepository.countByCourseId(courseId);
        Long totalAssessments = assessmentRepository.countByCourseId(courseId);
        Long totalDiscussions = discussionRepository.countByCourseId(courseId);
        
        // Calculate average progress for this course
        Double averageProgress = enrollmentRepository.getAverageProgressByCourseId(courseId);
        
        // Calculate average grade
        Double averageGrade = submissionRepository.getAverageGradeByCourseId(courseId);
        
        Integer totalSubmissions = submissionRepository.countByCourseId(courseId);
        
        return CourseAnalyticsDTO.builder()
                .courseId(courseId)
                .courseTitle(courseRepository.findById(courseId).map(c -> c.getTitle()).orElse(""))
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .completionRate(completionRate)
                .totalLessons(totalLessons)
                .totalAssessments(totalAssessments)
                .totalDiscussions(totalDiscussions)
                .averageProgress(averageProgress != null ? averageProgress : 0.0)
                .averageGrade(averageGrade != null ? averageGrade : 0.0)
                .totalSubmissions(totalSubmissions)
                .build();
    }
    
    /**
     * Get student analytics
     */
    public StudentAnalyticsDTO getStudentAnalytics(Long studentId) {
        log.info("Getting analytics for student: {}", studentId);
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new BadRequestException("User is not a student");
        }
        
        Integer totalEnrollments = (int) enrollmentRepository.countByStudentId(studentId);
        Integer activeEnrollments = (int) enrollmentRepository.countByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);
        Integer completedCourses = (int) enrollmentRepository.countByStudentIdAndStatus(studentId, EnrollmentStatus.COMPLETED);
        
        Double overallProgress = enrollmentRepository.getAverageProgressByStudentId(studentId);
        Integer totalLessonsCompleted = (int) lessonCompletionRepository.countByStudentId(studentId);
        
        Double averageGrade = submissionRepository.getAverageGradeByStudentId(studentId);
        Integer totalSubmissions = (int) submissionRepository.countByStudentId(studentId);
        
        Integer totalDiscussions = (int) discussionRepository.countByUserId(studentId);
        
        LocalDateTime lastActivity = enrollmentRepository.getLastActivityByStudentId(studentId);
        
        return StudentAnalyticsDTO.builder()
                .studentId(studentId)
                .studentName(student.getFirstName() + " " + student.getLastName())
                .email(student.getEmail())
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedCourses(completedCourses)
                .overallProgress(overallProgress != null ? overallProgress : 0.0)
                .totalLessonsCompleted(totalLessonsCompleted)
                .averageGrade(averageGrade != null ? averageGrade : 0.0)
                .totalSubmissions(totalSubmissions)
                .totalDiscussions(totalDiscussions)
                .lastActivity(lastActivity)
                .build();
    }
    
    /**
     * Get instructor dashboard analytics
     */
    public InstructorAnalyticsDTO getInstructorAnalytics(String username) {
        log.info("Getting analytics for instructor: {}", username);
        
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        
        if (!instructor.getRole().equals(Role.INSTRUCTOR) && !instructor.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("User is not an instructor");
        }
        
        Integer totalCourses = (int) courseRepository.countByInstructorId(instructor.getId());
        Integer publishedCourses = (int) courseRepository.countByInstructorIdAndIsPublished(instructor.getId(), true);
        Integer draftCourses = totalCourses - publishedCourses;
        
        Long totalStudents = enrollmentRepository.countDistinctStudentsByInstructorId(instructor.getId());
        Long totalEnrollments = enrollmentRepository.countByInstructorId(instructor.getId());
        
        Integer totalLessons = (int) lessonRepository.countByInstructorId(instructor.getId());
        Integer totalAssessments = (int) assessmentRepository.countByInstructorId(instructor.getId());
        Integer totalDiscussions = (int) discussionRepository.countByInstructorId(instructor.getId());
        
        Double averageCompletionRate = enrollmentRepository.getAverageCompletionRateByInstructorId(instructor.getId());
        Double averageStudentGrade = submissionRepository.getAverageGradeByInstructorId(instructor.getId());
        
        return InstructorAnalyticsDTO.builder()
                .instructorId(instructor.getId())
                .instructorName(instructor.getFirstName() + " " + instructor.getLastName())
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .draftCourses(draftCourses)
                .totalStudents(totalStudents)
                .totalEnrollments(totalEnrollments)
                .totalLessons(totalLessons)
                .totalAssessments(totalAssessments)
                .totalDiscussions(totalDiscussions)
                .averageCompletionRate(averageCompletionRate != null ? averageCompletionRate : 0.0)
                .averageStudentGrade(averageStudentGrade != null ? averageStudentGrade : 0.0)
                .build();
    }
    
    /**
     * Get system-wide analytics (Admin only)
     */
    public SystemAnalyticsDTO getSystemAnalytics(String username) {
        log.info("Getting system analytics");
        
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException("Only admins can view system analytics");
        }
        
        Long totalUsers = userRepository.count();
        Long totalStudents = userRepository.countByRole(Role.STUDENT);
        Long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        Long totalAdmins = userRepository.countByRole(Role.ADMIN);
        
        Long totalCourses = courseRepository.count();
        Long publishedCourses = courseRepository.countByIsPublished(true);
        Long totalCategories = categoryRepository.count();
        
        Long totalEnrollments = enrollmentRepository.count();
        Long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        Long completedEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);
        
        Double overallCompletionRate = totalEnrollments > 0
            ? (completedEnrollments * 100.0) / totalEnrollments
            : 0.0;
        
        Long totalLessons = lessonRepository.count();
        Long totalAssessments = assessmentRepository.count();
        Long totalDiscussions = discussionRepository.count();
        
        Long totalSubmissions = submissionRepository.count();
        Long totalCompletions = lessonCompletionRepository.count();
        Long totalNotifications = notificationRepository.count();
        
        Double averageGrade = submissionRepository.getAverageGrade();
        
        return SystemAnalyticsDTO.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalAdmins(totalAdmins)
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .totalCategories(totalCategories)
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .overallCompletionRate(overallCompletionRate)
                .totalLessons(totalLessons)
                .totalAssessments(totalAssessments)
                .totalDiscussions(totalDiscussions)
                .totalSubmissions(totalSubmissions)
                .totalCompletions(totalCompletions)
                .totalNotifications(totalNotifications)
                .averageGrade(averageGrade != null ? averageGrade : 0.0)
                .build();
    }
}
