package com.edusmart.repository;

import com.edusmart.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Invoice entity
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByOrderId(Long orderId);

    @Query("SELECT i FROM Invoice i WHERE i.order.student.id = :studentId ORDER BY i.invoiceDate DESC")
    Page<Invoice> findByStudentId(Long studentId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.isPaid = :isPaid ORDER BY i.invoiceDate DESC")
    Page<Invoice> findByIsPaid(Boolean isPaid, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate ORDER BY i.invoiceDate DESC")
    List<Invoice> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.isPaid = true AND i.paidAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.isPaid = :isPaid")
    long countByIsPaid(Boolean isPaid);
}
