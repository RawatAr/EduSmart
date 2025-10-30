package com.edusmart.entity.enums;

/**
 * Order status enumeration
 */
public enum OrderStatus {
    PENDING,        // Order created, awaiting payment
    PROCESSING,     // Payment received, processing enrollment
    COMPLETED,      // Order fulfilled, courses enrolled
    FAILED,         // Payment or processing failed
    REFUNDED,       // Order refunded
    CANCELLED       // Order cancelled by user
}
