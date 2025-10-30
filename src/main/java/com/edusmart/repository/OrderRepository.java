package com.edusmart.repository;

import com.edusmart.entity.Order;
import com.edusmart.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    List<Order> findByStudentIdAndStatus(Long studentId, OrderStatus status);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.student.id = :studentId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByStudentAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.student.id = :studentId")
    long countByStudentId(Long studentId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.student.id = :studentId AND o.status = 'COMPLETED'")
    Double getTotalSpentByStudent(Long studentId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
