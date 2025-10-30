package com.edusmart.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Order item entity - Individual courses in an order
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"order", "course"})
@ToString(exclude = {"order", "course"})
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String courseTitle; // Store title for historical reference

    @Column(nullable = false)
    private Double price; // Price at time of purchase

    @Column(columnDefinition = "TEXT")
    private String courseDescription; // Store description for records
}
