package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Lesson;
import com.edusmart.edusmart.model.Progress;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.repository.LessonRepository;
import com.edusmart.edusmart.repository.ProgressRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    public Optional<Progress> getProgressById(UUID id) {
        return progressRepository.findById(id);
    }

    public Progress markLessonCompleted(UUID studentId, UUID lessonId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with ID: " + studentId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));

        Optional<Progress> existingProgress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (existingProgress.isPresent()) {
            Progress progress = existingProgress.get();
            progress.setCompleted(true);
            progress.setCompletionDate(LocalDateTime.now());
            return progressRepository.save(progress);
        } else {
            Progress progress = new Progress();
            progress.setStudent(student);
            progress.setLesson(lesson);
            progress.setCompleted(true);
            progress.setCompletionDate(LocalDateTime.now());
            return progressRepository.save(progress);
        }
    }

    public void deleteProgress(UUID id) {
        progressRepository.deleteById(id);
    }

    public List<Progress> getProgressByStudentId(UUID studentId) {
        return progressRepository.findByStudentId(studentId);
    }

    public List<Progress> getProgressByLessonId(UUID lessonId) {
        return progressRepository.findByLessonId(lessonId);
    }

    public double getCourseCompletionPercentage(UUID studentId, UUID courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        if (lessons.isEmpty()) {
            return 0.0;
        }

        long completedLessons = lessons.stream()
                .filter(lesson -> progressRepository.findByStudentIdAndLessonId(studentId, lesson.getId())
                        .map(Progress::getCompleted).orElse(false))
                .count();

        return (double) completedLessons / lessons.size() * 100;
    }
}