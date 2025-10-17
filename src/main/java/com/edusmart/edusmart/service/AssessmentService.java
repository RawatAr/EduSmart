package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Assessment;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.repository.AssessmentRepository;
import com.edusmart.edusmart.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;

    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    public Optional<Assessment> getAssessmentById(UUID id) {
        return assessmentRepository.findById(id);
    }

    public Assessment createAssessment(Assessment assessment, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        assessment.setCourse(course);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        return assessmentRepository.save(assessment);
    }

    public Assessment updateAssessment(UUID id, Assessment updatedAssessment, UUID courseId) {
        return assessmentRepository.findById(id)
                .map(assessment -> {
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
                    assessment.setTitle(updatedAssessment.getTitle());
                    assessment.setDescription(updatedAssessment.getDescription());
                    assessment.setType(updatedAssessment.getType());
                    assessment.setCourse(course);
                    assessment.setUpdatedAt(LocalDateTime.now());
                    return assessmentRepository.save(assessment);
                })
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + id));
    }

    public void deleteAssessment(UUID id) {
        assessmentRepository.deleteById(id);
    }

    public List<Assessment> getAssessmentsByCourseId(UUID courseId) {
        return assessmentRepository.findByCourseId(courseId);
    }
}