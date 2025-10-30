package com.edusmart.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for applying coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyCouponRequestDTO {
    
    @NotBlank(message = "Coupon code is required")
    private String couponCode;
}
