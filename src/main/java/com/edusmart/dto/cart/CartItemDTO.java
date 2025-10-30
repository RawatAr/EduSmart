package com.edusmart.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cart item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private Double price;
    private Double discountPrice;
    private String thumbnailUrl;
}
