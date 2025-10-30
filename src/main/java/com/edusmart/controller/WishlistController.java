package com.edusmart.controller;

import com.edusmart.dto.wishlist.WishlistResponseDTO;
import com.edusmart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for wishlist management
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    /**
     * Add course to wishlist
     */
    @PostMapping("/add/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WishlistResponseDTO> addToWishlist(
            @PathVariable Long courseId,
            Authentication authentication) {
        WishlistResponseDTO response = wishlistService.addToWishlist(courseId, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Remove course from wishlist
     */
    @DeleteMapping("/remove/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long courseId,
            Authentication authentication) {
        wishlistService.removeFromWishlist(courseId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get my wishlist
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<WishlistResponseDTO>> getMyWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WishlistResponseDTO> wishlist = wishlistService.getMyWishlist(authentication.getName(), pageable);
        return ResponseEntity.ok(wishlist);
    }
    
    /**
     * Check if course is in wishlist
     */
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> isInWishlist(
            @PathVariable Long courseId,
            Authentication authentication) {
        boolean inWishlist = wishlistService.isInWishlist(courseId, authentication.getName());
        return ResponseEntity.ok(inWishlist);
    }
    
    /**
     * Get wishlist count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Long> getWishlistCount(Authentication authentication) {
        long count = wishlistService.getWishlistCount(authentication.getName());
        return ResponseEntity.ok(count);
    }
}
