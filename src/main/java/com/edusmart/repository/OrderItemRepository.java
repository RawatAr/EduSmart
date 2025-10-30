package com.edusmart.repository;

import com.edusmart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OrderItem entity
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByCourseId(Long courseId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.course.id = :courseId")
    long countByCourseId(Long courseId);

    @Query("SELECT SUM(oi.price) FROM OrderItem oi WHERE oi.course.id = :courseId")
    Double getTotalRevenueForCourse(Long courseId);
}
