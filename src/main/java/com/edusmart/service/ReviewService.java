package com.edusmart.service;

import com.edusmart.dto.review.ReviewRequestDTO;
import com.edusmart.dto.review.ReviewResponseDTO;
import com.edusmart.entity.Course;
import com.edusmart.entity.CourseReview;
import com.edusmart.entity.Enrollment;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.EnrollmentStatus;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.CourseReviewRepository;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for course reviews and ratings
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {
    
    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    
    /**
     * Create or update a review
     */
    public ReviewResponseDTO createOrUpdateReview(ReviewRequestDTO request, String username) {
        log.info("Creating/updating review for course: {}", request.getCourseId());
        
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        // Check if student is enrolled
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new BadRequestException("You must be enrolled to review this course"));
        
        // Check if existing review
        CourseReview review = reviewRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElse(CourseReview.builder()
                        .student(student)
                        .course(course)
                        .build());
        
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        
        // Verify if student completed the course
        review.setIsVerified(enrollment.getStatus() == EnrollmentStatus.COMPLETED);
        
        review = reviewRepository.save(review);
        
        // Update course average rating
        updateCourseRating(course.getId());
        
        return mapToDTO(review);
    }
    
    /**
     * Get course reviews
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getCourseReviews(Long courseId, Pageable pageable) {
        log.info("Getting reviews for course: {}", courseId);
        
        return reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get verified reviews only
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getVerifiedReviews(Long courseId, Pageable pageable) {
        return reviewRepository.findVerifiedReviewsByCourseId(courseId, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Mark review as helpful
     */
    public void markHelpful(Long reviewId) {
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }
    
    /**
     * Delete review
     */
    public void deleteReview(Long reviewId, String username) {
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!review.getStudent().getId().equals(user.getId())) {
            throw new BadRequestException("You can only delete your own reviews");
        }
        
        reviewRepository.delete(review);
        updateCourseRating(review.getCourse().getId());
    }
    
    /**
     * Update course average rating
     */
    private void updateCourseRating(Long courseId) {
        Double avgRating = reviewRepository.getAverageRatingByCourseId(courseId);
        long reviewCount = reviewRepository.countByCourseId(courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        course.setAverageRating(avgRating != null ? avgRating : 0.0);
        course.setReviewCount((int) reviewCount);
        courseRepository.save(course);
    }
    
    /**
     * Map entity to DTO
     */
    private ReviewResponseDTO mapToDTO(CourseReview review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getFirstName() + " " + review.getStudent().getLastName())
                .courseId(review.getCourse().getId())
                .courseTitle(review.getCourse().getTitle())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .isVerified(review.getIsVerified())
                .helpfulCount(review.getHelpfulCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
