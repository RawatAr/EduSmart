package com.edusmart.edusmart.service;

import com.edusmart.edusmart.dto.CourseDTO;
import com.edusmart.edusmart.dto.CourseSearchDTO;
import com.edusmart.edusmart.model.Category;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.repository.CategoryRepository;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Cacheable(value = "course", key = "#id")
    public Optional<Course> getCourseById(UUID id) {
        return courseRepository.findById(id);
    }

    @CacheEvict(value = {"courses", "course"}, allEntries = true)
    public Course createCourse(Course course, UUID instructorId, UUID categoryId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new UsernameNotFoundException("Instructor not found with ID: " + instructorId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        course.setInstructor(instructor);
        course.setCategory(category);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @CacheEvict(value = {"courses", "course"}, allEntries = true)
    public Course updateCourse(UUID id, Course updatedCourse, UUID categoryId) {
        return courseRepository.findById(id)
                .map(course -> {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
                    course.setTitle(updatedCourse.getTitle());
                    course.setDescription(updatedCourse.getDescription());
                    course.setContent(updatedCourse.getContent());
                    course.setCategory(category);
                    course.setUpdatedAt(LocalDateTime.now());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    @CacheEvict(value = {"courses", "course"}, allEntries = true)
    public void deleteCourse(UUID id) {
        courseRepository.deleteById(id);
    }

    @Cacheable(value = "coursesByCategory", key = "#categoryId")
    public List<Course> getCoursesByCategoryId(UUID categoryId) {
        return courseRepository.findByCategoryId(categoryId);
    }

    @Cacheable(value = "coursesByInstructor", key = "#instructorId")
    public List<Course> getCoursesByInstructorId(UUID instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    // DTO conversion methods
    public CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .content(course.getContent())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                // Additional fields can be populated with actual data from repositories
                .build();
    }

    public Course convertToEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setContent(courseDTO.getContent());
        return course;
    }

    // Search and filtering methods
    public Page<CourseDTO> searchCourses(CourseSearchDTO searchDTO) {
        // Create pageable object
        Sort sort = Sort.by(Sort.Direction.fromString(searchDTO.getSortDirection()), searchDTO.getSortBy());
        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort);

        // Build specification for filtering
        Specification<Course> spec = Specification.where(null);

        if (searchDTO.getQuery() != null && !searchDTO.getQuery().trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + searchDTO.getQuery().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + searchDTO.getQuery().toLowerCase() + "%")
                )
            );
        }

        if (searchDTO.getCategoryId() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("category").get("id"), searchDTO.getCategoryId())
            );
        }

        if (searchDTO.getInstructorId() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("instructor").get("id"), searchDTO.getInstructorId())
            );
        }

        // Execute search
        Page<Course> coursePage = courseRepository.findAll(spec, pageable);

        // Convert to DTOs
        return coursePage.map(this::convertToDTO);
    }
}