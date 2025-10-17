package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.Enrollment;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.EnrollmentStatus;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.EnrollmentRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(UUID id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment enrollStudentInCourse(UUID studentId, UUID courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new RuntimeException("Student is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollmentStatus(UUID id, EnrollmentStatus status) {
        return enrollmentRepository.findById(id)
                .map(enrollment -> {
                    enrollment.setStatus(status);
                    return enrollmentRepository.save(enrollment);
                })
                .orElseThrow(() -> new RuntimeException("Enrollment not found with ID: " + id));
    }

    public List<Enrollment> getEnrollmentsByStudentId(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourseIdAndStatus(UUID courseId, EnrollmentStatus status) {
        return enrollmentRepository.findByCourseIdAndStatus(courseId, status);
    }

    public void deleteEnrollment(UUID id) {
        enrollmentRepository.deleteById(id);
    }
}