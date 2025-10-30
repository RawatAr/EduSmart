package com.edusmart.service;

import com.edusmart.dto.wishlist.WishlistResponseDTO;
import com.edusmart.entity.Course;
import com.edusmart.entity.User;
import com.edusmart.entity.Wishlist;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.UserRepository;
import com.edusmart.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for wishlist management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    /**
     * Add course to wishlist
     */
    public WishlistResponseDTO addToWishlist(Long courseId, String username) {
        log.info("Adding course {} to wishlist for user: {}", courseId, username);
        
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        // Check if already in wishlist
        if (wishlistRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BadRequestException("Course already in wishlist");
        }
        
        Wishlist wishlist = Wishlist.builder()
                .student(student)
                .course(course)
                .build();
        
        wishlist = wishlistRepository.save(wishlist);
        
        return mapToDTO(wishlist);
    }
    
    /**
     * Remove course from wishlist
     */
    public void removeFromWishlist(Long courseId, String username) {
        log.info("Removing course {} from wishlist for user: {}", courseId, username);
        
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        wishlistRepository.deleteByStudentIdAndCourseId(student.getId(), courseId);
    }
    
    /**
     * Get user's wishlist
     */
    @Transactional(readOnly = true)
    public Page<WishlistResponseDTO> getMyWishlist(String username, Pageable pageable) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return wishlistRepository.findByStudentIdOrderByCreatedAtDesc(student.getId(), pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Check if course is in wishlist
     */
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long courseId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return wishlistRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
    }
    
    /**
     * Get wishlist count
     */
    @Transactional(readOnly = true)
    public long getWishlistCount(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return wishlistRepository.countByStudentId(student.getId());
    }
    
    /**
     * Map to DTO
     */
    private WishlistResponseDTO mapToDTO(Wishlist wishlist) {
        Course course = wishlist.getCourse();
        
        return WishlistResponseDTO.builder()
                .id(wishlist.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getShortDescription())
                .instructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName())
                .averageRating(course.getAverageRating())
                .reviewCount(course.getReviewCount())
                .price(course.getPrice() != null ? course.getPrice().doubleValue() : 0.0)
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}
