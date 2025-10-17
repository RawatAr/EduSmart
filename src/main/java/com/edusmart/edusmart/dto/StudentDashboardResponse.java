package com.edusmart.edusmart.dto;

import com.edusmart.edusmart.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDashboardResponse {
    private String studentName;
    private String studentEmail;
    private List<Course> enrolledCourses;
    private long completedCourses;
    private double overallProgress;
    // Add more relevant fields as needed, e.g., upcoming assignments, notifications
}