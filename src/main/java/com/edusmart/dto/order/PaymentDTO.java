package com.edusmart.dto.order;

import com.edusmart.entity.enums.PaymentMethod;
import com.edusmart.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for payment information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    
    private Long id;
    private String transactionId;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Double amount;
    private String paymentGateway;
    private LocalDateTime paidAt;
}
