package com.edusmart.entity;

import com.edusmart.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Order entity - Represents a completed purchase
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student", "items", "payment", "appliedCoupon"})
@ToString(exclude = {"student", "items", "payment", "appliedCoupon"})
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, unique = true)
    private String orderNumber; // e.g., ORD-20250126-001

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double subtotal; // Total before discount

    @Column(nullable = false)
    private Double discountAmount; // Discount applied

    @Column(nullable = false)
    private Double taxAmount; // Tax amount (0 for now)

    @Column(nullable = false)
    private Double totalAmount; // Final amount paid

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon;

    @Column(name = "coupon_code")
    private String couponCode; // Store code for reference

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(columnDefinition = "TEXT")
    private String notes; // Additional notes or instructions
}
