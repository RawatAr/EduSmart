package com.edusmart.edusmart.service;

import com.edusmart.edusmart.dto.InstructorDashboardResponse;
import com.edusmart.edusmart.dto.StudentDashboardResponse;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.Enrollment;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.EnrollmentStatus;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.EnrollmentRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public InstructorDashboardResponse getInstructorDashboard(UUID instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new UsernameNotFoundException("Instructor not found with ID: " + instructorId));

        List<Course> coursesTaught = courseRepository.findByInstructorId(instructorId);
        long totalStudentsEnrolled = coursesTaught.stream()
                .map(course -> enrollmentRepository.findByCourseId(course.getId()).size())
                .mapToLong(Integer::longValue)
                .sum();

        return InstructorDashboardResponse.builder()
                .instructorName(instructor.getFirstName() + " " + instructor.getLastName())
                .instructorEmail(instructor.getEmail())
                .courses(coursesTaught)
                .totalStudentsEnrolled(totalStudentsEnrolled)
                .build();
    }

    public StudentDashboardResponse getStudentDashboard(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with ID: " + studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<Course> enrolledCourses = enrollments.stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());

        long completedCourses = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .count();

        // This is a simplified overall progress calculation.
        // A more robust implementation would involve tracking lesson completion, assessment scores, etc.
        double overallProgress = (double) completedCourses / enrolledCourses.size() * 100;
        if (enrolledCourses.isEmpty()) {
            overallProgress = 0.0;
        }

        return StudentDashboardResponse.builder()
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentEmail(student.getEmail())
                .enrolledCourses(enrolledCourses)
                .completedCourses(completedCourses)
                .overallProgress(overallProgress)
                .build();
    }
}