package com.edusmart.entity;

import com.edusmart.entity.enums.PaymentMethod;
import com.edusmart.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Payment entity - Tracks payment transactions
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"order"})
@ToString(exclude = {"order"})
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, unique = true)
    private String transactionId; // Payment gateway transaction ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_gateway")
    private String paymentGateway; // e.g., "Stripe", "PayPal"

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // Raw response from gateway

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(columnDefinition = "TEXT")
    private String failureReason; // Reason if payment failed
}
