package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.dto.AuthenticationRequest;
import com.edusmart.edusmart.dto.AuthenticationResponse;
import com.edusmart.edusmart.model.Category;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.Role;
import com.edusmart.edusmart.repository.CategoryRepository;
import com.edusmart.edusmart.repository.CourseRepository;
import com.edusmart.edusmart.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class CourseControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String jwtToken;
    private Course testCourse;
    private Category testCategory;
    private User testInstructor;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    // Ensure clean state
    courseRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    // Create test data
        testInstructor = new User();
        testInstructor.setUsername("testinstructor");
        testInstructor.setEmail("instructor@test.com");
        testInstructor.setPasswordHash(passwordEncoder.encode("password"));
        testInstructor.setRole(Role.INSTRUCTOR);
        testInstructor.setFirstName("Test");
        testInstructor.setLastName("Instructor");
        userRepository.save(testInstructor);

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test category description");
        categoryRepository.save(testCategory);

        testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test course description");
        testCourse.setContent("Test course content");
        testCourse.setInstructor(testInstructor);
        testCourse.setCategory(testCategory);
        courseRepository.save(testCourse);

        // Get JWT token for authentication
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setEmail("instructor@test.com");
        authRequest.setPassword("password");

        String authResponse = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthenticationResponse auth = objectMapper.readValue(authResponse, AuthenticationResponse.class);
        jwtToken = auth.getToken();
    }

    @Test
    void getAllCourses_ShouldReturnCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() throws Exception {
        mockMvc.perform(get("/api/courses/{id}", testCourse.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCourse.getId().toString()))
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getCourseById_WhenCourseDoesNotExist_ShouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/courses/{id}", nonExistentId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourse_WithValidData_ShouldReturnCreatedCourse() throws Exception {
        Course newCourse = new Course();
        newCourse.setTitle("New Test Course");
        newCourse.setDescription("New test course description");
        newCourse.setContent("New test course content");

        mockMvc.perform(post("/api/courses/instructor/{instructorId}/category/{categoryId}",
                testInstructor.getId(), testCategory.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Test Course"));
    }

    @Test
    void createCourse_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        Course newCourse = new Course();
        newCourse.setTitle("Unauthorized Course");
        newCourse.setDescription("Should not be created");

        mockMvc.perform(post("/api/courses/instructor/{instructorId}/category/{categoryId}",
                testInstructor.getId(), testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCourse_WithValidData_ShouldReturnUpdatedCourse() throws Exception {
        Course updatedCourse = new Course();
        updatedCourse.setTitle("Updated Course Title");
        updatedCourse.setDescription("Updated description");

        mockMvc.perform(put("/api/courses/{id}/category/{categoryId}",
                testCourse.getId(), testCategory.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Course Title"));
    }

    @Test
    void deleteCourse_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/courses/{id}", testCourse.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCoursesByCategoryId_ShouldReturnCoursesForCategory() throws Exception {
        mockMvc.perform(get("/api/courses/category/{categoryId}", testCategory.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCoursesByInstructorId_ShouldReturnCoursesForInstructor() throws Exception {
        mockMvc.perform(get("/api/courses/instructor/{instructorId}", testInstructor.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}