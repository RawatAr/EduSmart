package com.edusmart.service;

import com.edusmart.dto.cart.ApplyCouponRequestDTO;
import com.edusmart.dto.cart.CartItemDTO;
import com.edusmart.dto.cart.CartResponseDTO;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.DiscountType;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for shopping cart operations
 */
@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Get or create cart for student
     */
    @Transactional
    public Cart getOrCreateCart(Long studentId) {
        return cartRepository.findByStudentId(studentId)
            .orElseGet(() -> {
                User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
                
                Cart cart = Cart.builder()
                    .student(student)
                    .totalAmount(0.0)
                    .discountAmount(0.0)
                    .finalAmount(0.0)
                    .build();
                
                return cartRepository.save(cart);
            });
    }
    
    /**
     * Add course to cart
     */
    @Transactional
    public CartResponseDTO addToCart(Long studentId, Long courseId) {
        Cart cart = getOrCreateCart(studentId);
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Already enrolled in this course");
        }
        
        // Check if already in cart
        if (cartItemRepository.existsByCartIdAndCourseId(cart.getId(), courseId)) {
            throw new IllegalStateException("Course already in cart");
        }
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        CartItem item = CartItem.builder()
            .cart(cart)
            .course(course)
            .price(course.getPrice() != null ? course.getPrice().doubleValue() : 0.0)
            .build();
        
        cartItemRepository.save(item);
        
        // Recalculate cart totals
        recalculateCart(cart);
        
        return buildCartResponse(cart);
    }
    
    /**
     * Remove course from cart
     */
    @Transactional
    public CartResponseDTO removeFromCart(Long studentId, Long courseId) {
        Cart cart = getOrCreateCart(studentId);
        
        cartItemRepository.deleteByCartIdAndCourseId(cart.getId(), courseId);
        
        // Recalculate cart totals
        recalculateCart(cart);
        
        return buildCartResponse(cart);
    }
    
    /**
     * Get cart details
     */
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long studentId) {
        Cart cart = getOrCreateCart(studentId);
        return buildCartResponse(cart);
    }
    
    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(Long studentId) {
        Cart cart = getOrCreateCart(studentId);
        cart.getItems().clear();
        cart.setAppliedCoupon(null);
        recalculateCart(cart);
    }
    
    /**
     * Apply coupon to cart
     */
    @Transactional
    public CartResponseDTO applyCoupon(Long studentId, ApplyCouponRequestDTO request) {
        Cart cart = getOrCreateCart(studentId);
        
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid coupon code"));
        
        // Validate coupon
        validateCoupon(coupon, cart);
        
        cart.setAppliedCoupon(coupon);
        recalculateCart(cart);
        
        return buildCartResponse(cart);
    }
    
    /**
     * Remove coupon from cart
     */
    @Transactional
    public CartResponseDTO removeCoupon(Long studentId) {
        Cart cart = getOrCreateCart(studentId);
        cart.setAppliedCoupon(null);
        recalculateCart(cart);
        return buildCartResponse(cart);
    }
    
    /**
     * Validate coupon
     */
    private void validateCoupon(Coupon coupon, Cart cart) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if active
        if (!coupon.getIsActive()) {
            throw new IllegalStateException("Coupon is not active");
        }
        
        // Check validity period
        if (now.isBefore(coupon.getValidFrom())) {
            throw new IllegalStateException("Coupon is not yet valid");
        }
        
        if (now.isAfter(coupon.getValidUntil())) {
            throw new IllegalStateException("Coupon has expired");
        }
        
        // Check usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalStateException("Coupon usage limit reached");
        }
        
        // Calculate current total
        Double total = cart.getItems().stream()
            .mapToDouble(CartItem::getPrice)
            .sum();
        
        // Check minimum purchase
        if (coupon.getMinPurchaseAmount() != null && total < coupon.getMinPurchaseAmount()) {
            throw new IllegalStateException("Minimum purchase amount not met");
        }
    }
    
    /**
     * Recalculate cart totals
     */
    private void recalculateCart(Cart cart) {
        Double total = cart.getItems().stream()
            .mapToDouble(CartItem::getPrice)
            .sum();
        
        cart.setTotalAmount(total);
        
        Double discount = 0.0;
        
        if (cart.getAppliedCoupon() != null) {
            Coupon coupon = cart.getAppliedCoupon();
            
            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                discount = total * (coupon.getDiscountValue() / 100.0);
            } else {
                discount = coupon.getDiscountValue();
            }
            
            // Apply max discount cap if exists
            if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                discount = coupon.getMaxDiscountAmount();
            }
            
            // Discount cannot exceed total
            if (discount > total) {
                discount = total;
            }
        }
        
        cart.setDiscountAmount(discount);
        cart.setFinalAmount(total - discount);
        
        cartRepository.save(cart);
    }
    
    /**
     * Build cart response DTO
     */
    private CartResponseDTO buildCartResponse(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
            .map(this::buildCartItemDTO)
            .collect(Collectors.toList());
        
        return CartResponseDTO.builder()
            .id(cart.getId())
            .items(itemDTOs)
            .totalAmount(cart.getTotalAmount())
            .discountAmount(cart.getDiscountAmount())
            .finalAmount(cart.getFinalAmount())
            .appliedCouponCode(cart.getAppliedCoupon() != null ? cart.getAppliedCoupon().getCode() : null)
            .itemCount(itemDTOs.size())
            .build();
    }
    
    /**
     * Build cart item DTO
     */
    private CartItemDTO buildCartItemDTO(CartItem item) {
        Course course = item.getCourse();
        User instructor = course.getInstructor();
        
        return CartItemDTO.builder()
            .id(item.getId())
            .courseId(course.getId())
            .courseTitle(course.getTitle())
            .instructorName(instructor != null ? instructor.getFullName() : "Unknown")
            .price(item.getPrice())
            .discountPrice(item.getDiscountPrice())
            .thumbnailUrl(course.getThumbnailUrl())
            .build();
    }
}
