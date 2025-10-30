package com.edusmart.service;

import com.edusmart.dto.admin.DashboardStatsDTO;
import com.edusmart.dto.admin.TopCourseDTO;
import com.edusmart.dto.admin.TopInstructorDTO;
import com.edusmart.entity.enums.EnrollmentStatus;
import com.edusmart.entity.enums.OrderStatus;
import com.edusmart.entity.enums.Role;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for admin analytics and dashboard
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminAnalyticsService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final CertificateRepository certificateRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Get dashboard statistics
     */
    public DashboardStatsDTO getDashboardStats() {
        log.info("Fetching dashboard statistics");

        // User stats
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        // Course stats
        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.count(); // All courses (simplified)
        long draftCourses = 0L; // Simplified - can be enhanced later

        // Enrollment stats
        long totalEnrollments = enrollmentRepository.count();
        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long completedEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);

        // Revenue stats
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.now();

        Double totalRevenue = orderRepository.getTotalRevenue(
            LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.MIN),
            LocalDateTime.now()
        );
        Double monthlyRevenue = orderRepository.getTotalRevenue(startOfMonth, endOfMonth);
        Double todayRevenue = orderRepository.getTotalRevenue(startOfToday, endOfToday);

        // Order stats
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long failedOrders = orderRepository.countByStatus(OrderStatus.FAILED);

        // Other stats
        long totalReviews = courseReviewRepository.count();
        Double averageCourseRating = 0.0; // Calculated separately if needed
        long totalCertificates = certificateRepository.count();

        return DashboardStatsDTO.builder()
            .totalUsers(totalUsers)
            .totalStudents(totalStudents)
            .totalInstructors(totalInstructors)
            .totalAdmins(totalAdmins)
            .totalCourses(totalCourses)
            .publishedCourses(publishedCourses)
            .draftCourses(draftCourses)
            .totalEnrollments(totalEnrollments)
            .activeEnrollments(activeEnrollments)
            .completedEnrollments(completedEnrollments)
            .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
            .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : 0.0)
            .todayRevenue(todayRevenue != null ? todayRevenue : 0.0)
            .totalOrders(totalOrders)
            .pendingOrders(pendingOrders)
            .completedOrders(completedOrders)
            .failedOrders(failedOrders)
            .totalReviews(totalReviews)
            .averageCourseRating(averageCourseRating != null ? averageCourseRating : 0.0)
            .totalCertificates(totalCertificates)
            .build();
    }

    /**
     * Get top performing courses
     */
    public List<TopCourseDTO> getTopCourses(int limit) {
        log.info("Fetching top {} courses", limit);
        
        // This is a simplified version. In production, you'd use a custom query
        List<TopCourseDTO> topCourses = new ArrayList<>();
        
        // TODO: Implement complex query to get top courses by enrollment and revenue
        // For now, return empty list as placeholder
        
        return topCourses;
    }

    /**
     * Get top performing instructors
     */
    public List<TopInstructorDTO> getTopInstructors(int limit) {
        log.info("Fetching top {} instructors", limit);
        
        // This is a simplified version. In production, you'd use a custom query
        List<TopInstructorDTO> topInstructors = new ArrayList<>();
        
        // TODO: Implement complex query to get top instructors by revenue and students
        // For now, return empty list as placeholder
        
        return topInstructors;
    }

    /**
     * Get revenue for date range
     */
    public Double getRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = orderRepository.getTotalRevenue(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    /**
     * Get total payments for period
     */
    public Double getTotalPayments(LocalDateTime startDate, LocalDateTime endDate) {
        Double payments = paymentRepository.getTotalPayments(startDate, endDate);
        return payments != null ? payments : 0.0;
    }
}
