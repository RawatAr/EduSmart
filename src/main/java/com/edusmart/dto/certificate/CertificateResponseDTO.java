package com.edusmart.dto.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for certificate response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponseDTO {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private String certificateNumber;
    private LocalDateTime issuedDate;
    private LocalDateTime completionDate;
    private Double finalGrade;
    private String certificateUrl;
    private String verificationCode;
}
