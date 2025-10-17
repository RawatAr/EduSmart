package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.dto.CourseSearchDTO;
import com.edusmart.edusmart.dto.RegisterRequest;
import com.edusmart.edusmart.model.Course;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.service.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final DashboardService dashboardService;
    private final EnrollmentService enrollmentService;
    private final UserProfileService userProfileService;

    /**
     * Home page with course catalog
     */
    @GetMapping("/")
    public String home(@RequestParam(required = false) String query,
                      @RequestParam(required = false) UUID categoryId,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "12") int size,
                      Model model) {

        // Create a CourseSearchDTO for pagination
        CourseSearchDTO searchDTO = CourseSearchDTO.builder()
                .query(query)
                .categoryId(categoryId)
                .page(page)
                .size(size)
                .sortBy("createdAt")
                .sortDirection("DESC")
                .build();

        var coursesPage = courseService.searchCourses(searchDTO);

        model.addAttribute("courses", coursesPage);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "index";
    }

    /**
     * Course detail page
     */
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable UUID id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        Optional<Course> courseOpt = courseService.getCourseById(id);
        if (courseOpt.isEmpty()) {
            return "redirect:/";
        }

        Course course = courseOpt.get();
        boolean isEnrolled = false;
        int progressPercentage = 0;
        int enrollmentCount = 25; // Placeholder

        // Mock enrollment check for demo
        if (userDetails != null) {
            isEnrolled = Math.random() > 0.5; // Random for demo
            if (isEnrolled) {
                progressPercentage = (int) (Math.random() * 100);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("progressPercentage", progressPercentage);
        model.addAttribute("enrollmentCount", enrollmentCount);
        model.addAttribute("lessons", List.of()); // Placeholder
        model.addAttribute("reviews", List.of()); // Placeholder
        model.addAttribute("relatedCourses", List.of()); // Placeholder

        return "course/detail";
    }

    /**
     * Student dashboard
     */
    @GetMapping("/dashboard/student")
    public String studentDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {

        // Create mock dashboard data
        var dashboard = new Object() {
            public int getTotalEnrollments() { return 5; }
            public int getCompletedCourses() { return 2; }
            public double getAverageProgress() { return 65.0; }
            public int getCertificatesEarned() { return 2; }
            public List<?> getEnrolledCourses() { return List.of(); }
        };

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("certificates", List.of());
        model.addAttribute("recentActivities", List.of());

        return "dashboard/student";
    }

    /**
     * Instructor dashboard
     */
    @GetMapping("/dashboard/instructor")
    public String instructorDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {

        // Create mock dashboard data
        var dashboard = new Object() {
            public int getTotalCourses() { return 3; }
            public int getTotalStudents() { return 45; }
            public int getTotalEnrollments() { return 67; }
            public double getAverageRating() { return 4.5; }
            public List<?> getCourses() { return List.of(); }
        };

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("recentActivities", List.of());

        return "dashboard/instructor";
    }

    /**
     * Login page
     */
    @GetMapping("/login-page")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }
        return "auth/login";
    }

    /**
     * Register page
     */
    @GetMapping("/register-page")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    /**
     * User profile page
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            // Try to get user by email/username
            try {
                User user = userProfileService.getUserProfile(UUID.randomUUID()); // Placeholder
                model.addAttribute("user", user);
            } catch (Exception e) {
                model.addAttribute("user", userDetails);
            }
        }
        return "user/profile";
    }

    /**
     * Certificates page
     */
    @GetMapping("/certificates")
    public String certificates(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("certificates", List.of()); // Placeholder
        return "user/certificates";
    }

    /**
     * Course learning page
     */
    @GetMapping("/courses/{courseId}/learn")
    public String learnCourse(@PathVariable UUID courseId,
                             @RequestParam(required = false) UUID lessonId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        Optional<Course> courseOpt = courseService.getCourseById(courseId);
        if (courseOpt.isEmpty()) {
            return "redirect:/";
        }

        Course course = courseOpt.get();

        model.addAttribute("course", course);
        model.addAttribute("lessons", List.of()); // Placeholder
        model.addAttribute("currentLessonId", lessonId);
        model.addAttribute("currentLesson", null); // Placeholder

        return "course/learn";
    }

    /**
     * Assessment/Quiz page
     */
    @GetMapping("/assessments/{assessmentId}/take")
    public String takeAssessment(@PathVariable UUID assessmentId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {

        model.addAttribute("assessment", null); // Placeholder
        model.addAttribute("questions", List.of()); // Placeholder

        return "assessment/take";
    }
}