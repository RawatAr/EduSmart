package com.edusmart.dto.order;

import com.edusmart.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    
    private Long id;
    private String orderNumber;
    private List<OrderItemDTO> items;
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private OrderStatus status;
    private String couponCode;
    private LocalDateTime createdAt;
    private PaymentDTO payment;
}
