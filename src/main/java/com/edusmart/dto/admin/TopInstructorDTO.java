package com.edusmart.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for top performing instructors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopInstructorDTO {
    
    private Long instructorId;
    private String instructorName;
    private String email;
    private Long totalCourses;
    private Long totalStudents;
    private Double totalRevenue;
    private Double averageRating;
}
