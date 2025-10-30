package com.edusmart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Invoice entity for order receipts
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"order"})
@ToString(exclude = {"order"})
public class Invoice extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // e.g., INV-20251026-001

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Double taxAmount;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(name = "pdf_path")
    private String pdfPath; // Path to generated PDF

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
