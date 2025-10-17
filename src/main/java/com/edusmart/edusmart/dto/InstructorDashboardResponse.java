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
public class InstructorDashboardResponse {
    private String instructorName;
    private String instructorEmail;
    private List<Course> courses;
    private long totalStudentsEnrolled;
    // Add more relevant fields as needed, e.g., course analytics, recent activities
}