package com.edusmart.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for wishlist response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDTO {
    
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String instructorName;
    private Double averageRating;
    private Integer reviewCount;
    private Double price;
    private LocalDateTime addedAt;
}
