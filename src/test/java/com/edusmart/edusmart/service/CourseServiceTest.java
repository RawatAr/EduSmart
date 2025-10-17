package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Category;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.Role;
import com.edusmart.edusmart.repository.CategoryRepository;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private Category testCategory;
    private User testInstructor;
    private UUID courseId;
    private UUID categoryId;
    private UUID instructorId;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        instructorId = UUID.randomUUID();

        testInstructor = new User();
        testInstructor.setId(instructorId);
        testInstructor.setUsername("instructor");
        testInstructor.setEmail("instructor@test.com");
        testInstructor.setRole(Role.INSTRUCTOR);

        testCategory = new Category();
        testCategory.setId(categoryId);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test category description");

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test course description");
        testCourse.setContent("Test course content");
        testCourse.setInstructor(testInstructor);
        testCourse.setCategory(testCategory);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Given
        List<Course> courses = List.of(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        // When
        List<Course> result = courseService.getAllCourses();

        // Then
        assertThat(result).isEqualTo(courses);
        verify(courseRepository).findAll();
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));

        // When
        Optional<Course> result = courseService.getCourseById(courseId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testCourse);
        verify(courseRepository).findById(courseId);
    }

    @Test
    void getCourseById_WhenCourseDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When
        Optional<Course> result = courseService.getCourseById(courseId);

        // Then
        assertThat(result).isEmpty();
        verify(courseRepository).findById(courseId);
    }

    @Test
    void createCourse_WithValidData_ShouldReturnCreatedCourse() {
        // Given
        when(userRepository.findById(instructorId)).thenReturn(Optional.of(testInstructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course result = courseService.createCourse(testCourse, instructorId, categoryId);

        // Then
        assertThat(result).isEqualTo(testCourse);
        verify(userRepository).findById(instructorId);
        verify(categoryRepository).findById(categoryId);
        verify(courseRepository).save(testCourse);
    }

    @Test
    void createCourse_WithInvalidCategory_ShouldThrowException() {
        // Given
        when(userRepository.findById(instructorId)).thenReturn(Optional.of(testInstructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.createCourse(testCourse, instructorId, categoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with ID: " + categoryId);
        verify(userRepository).findById(instructorId);
        verify(categoryRepository).findById(categoryId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void updateCourse_WithValidData_ShouldReturnUpdatedCourse() {
        // Given
        Course updatedCourse = new Course();
        updatedCourse.setTitle("Updated Course");
        updatedCourse.setDescription("Updated description");
        updatedCourse.setContent("Updated content");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course result = courseService.updateCourse(courseId, updatedCourse, categoryId);

        // Then
        assertThat(result).isEqualTo(testCourse);
        verify(courseRepository).findById(courseId);
        verify(categoryRepository).findById(categoryId);
        verify(courseRepository).save(testCourse);
    }

    @Test
    void updateCourse_WithInvalidCourseId_ShouldThrowException() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.updateCourse(courseId, testCourse, categoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found with ID: " + courseId);
        verify(courseRepository).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourse_WithValidId_ShouldDeleteCourse() {
        // When
        courseService.deleteCourse(courseId);

        // Then
        verify(courseRepository).deleteById(courseId);
    }

    @Test
    void getCoursesByCategoryId_ShouldReturnCoursesForCategory() {
        // Given
        List<Course> courses = List.of(testCourse);
        when(courseRepository.findByCategoryId(categoryId)).thenReturn(courses);

        // When
        List<Course> result = courseService.getCoursesByCategoryId(categoryId);

        // Then
        assertThat(result).isEqualTo(courses);
        verify(courseRepository).findByCategoryId(categoryId);
    }

    @Test
    void getCoursesByInstructorId_ShouldReturnCoursesForInstructor() {
        // Given
        List<Course> courses = List.of(testCourse);
        when(courseRepository.findByInstructorId(instructorId)).thenReturn(courses);

        // When
        List<Course> result = courseService.getCoursesByInstructorId(instructorId);

        // Then
        assertThat(result).isEqualTo(courses);
        verify(courseRepository).findByInstructorId(instructorId);
    }
}