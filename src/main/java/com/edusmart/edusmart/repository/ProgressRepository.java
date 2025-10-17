package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    long countByStudentIdAndLessonCourseIdAndCompleted(UUID studentId, UUID courseId, boolean completed);

    Optional<Progress> findByStudentIdAndLessonId(UUID studentId, UUID lessonId);

    List<Progress> findByStudentId(UUID studentId);

    List<Progress> findByLessonId(UUID lessonId);
}