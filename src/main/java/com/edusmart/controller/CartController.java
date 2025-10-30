package com.edusmart.controller;

import com.edusmart.dto.cart.ApplyCouponRequestDTO;
import com.edusmart.dto.cart.CartResponseDTO;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for shopping cart operations
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    /**
     * Add course to cart
     */
    @PostMapping("/add/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CartResponseDTO> addToCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long courseId) {
        CartResponseDTO response = cartService.addToCart(currentUser.getId(), courseId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove course from cart
     */
    @DeleteMapping("/remove/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long courseId) {
        CartResponseDTO response = cartService.removeFromCart(currentUser.getId(), courseId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get cart details
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponseDTO response = cartService.getCart(currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Clear cart
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Apply coupon to cart
     */
    @PostMapping("/apply-coupon")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CartResponseDTO> applyCoupon(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ApplyCouponRequestDTO request) {
        CartResponseDTO response = cartService.applyCoupon(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove coupon from cart
     */
    @DeleteMapping("/remove-coupon")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CartResponseDTO> removeCoupon(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponseDTO response = cartService.removeCoupon(currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get cart item count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Integer> getCartCount(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponseDTO cart = cartService.getCart(currentUser.getId());
        return ResponseEntity.ok(cart.getItemCount());
    }
}
