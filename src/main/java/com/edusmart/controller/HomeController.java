package com.edusmart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student-dashboard";
    }

    @GetMapping("/instructor/dashboard")
    public String instructorDashboard() {
        return "instructor-dashboard-new";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/courses")
    public String courses() {
        return "courses";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    @GetMapping("/assignments")
    public String assignments() {
        return "assignments";
    }
    
    @GetMapping("/quizzes")
    public String quizzes() {
        return "quizzes";
    }
    
    @GetMapping("/discussions")
    public String discussions() {
        return "discussions";
    }
    
    @GetMapping("/progress")
    public String progress() {
        return "progress";
    }
    
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
    
    @GetMapping("/certificates")
    public String certificates() {
        return "certificates";
    }
    
    @GetMapping("/course-detail")
    public String courseDetail() {
        return "course-detail";
    }
}
