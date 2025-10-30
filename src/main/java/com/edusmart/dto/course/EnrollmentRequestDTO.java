package com.edusmart.dto.course;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for course enrollment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    private String paymentMethod;
    
    private String transactionId;
}
