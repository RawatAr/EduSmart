package com.edusmart.edusmart.repository;

import com.edusmart.edusmart.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    List<Course> findByCategoryId(UUID categoryId);
    List<Course> findByInstructorId(UUID instructorId);
}