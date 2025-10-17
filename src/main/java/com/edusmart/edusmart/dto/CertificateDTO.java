package com.edusmart.edusmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {

    private UUID id;

    @NotNull(message = "User ID is required")
    private UUID userId;

    private String userName;

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    private String courseTitle;

    @NotBlank(message = "Certificate number is required")
    private String certificateNumber;

    private LocalDateTime issuedDate;

    private Double finalScore;

    private String grade;

    private String certificateUrl;

    // Additional metadata
    private String instructorName;
    private Integer courseDurationHours;
    private LocalDateTime completionDate;
}