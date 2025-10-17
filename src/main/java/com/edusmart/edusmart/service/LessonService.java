package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.Lesson;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> getLessonById(UUID id) {
        return lessonRepository.findById(id);
    }

    public Lesson createLesson(Lesson lesson, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        lesson.setCourse(course);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(UUID id, Lesson updatedLesson, UUID courseId) {
        return lessonRepository.findById(id)
                .map(lesson -> {
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
                    lesson.setTitle(updatedLesson.getTitle());
                    lesson.setContent(updatedLesson.getContent());
                    lesson.setOrderIndex(updatedLesson.getOrderIndex());
                    lesson.setCourse(course);
                    lesson.setUpdatedAt(LocalDateTime.now());
                    return lessonRepository.save(lesson);
                })
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + id));
    }

    public void deleteLesson(UUID id) {
        lessonRepository.deleteById(id);
    }

    public List<Lesson> getLessonsByCourseId(UUID courseId) {
        return lessonRepository.findByCourseId(courseId);
    }
}