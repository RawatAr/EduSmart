package com.edusmart.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for cart response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    
    private Long id;
    private List<CartItemDTO> items;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String appliedCouponCode;
    private Integer itemCount;
}
