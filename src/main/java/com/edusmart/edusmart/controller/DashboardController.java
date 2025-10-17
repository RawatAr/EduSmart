package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.dto.InstructorDashboardResponse;
import com.edusmart.edusmart.dto.StudentDashboardResponse;
import com.edusmart.edusmart.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<InstructorDashboardResponse> getInstructorDashboard(@PathVariable UUID instructorId) {
        return ResponseEntity.ok(dashboardService.getInstructorDashboard(instructorId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@PathVariable UUID studentId) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(studentId));
    }
}