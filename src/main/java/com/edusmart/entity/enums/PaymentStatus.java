package com.edusmart.entity.enums;

/**
 * Payment status enumeration
 */
public enum PaymentStatus {
    PENDING,        // Payment initiated
    PROCESSING,     // Payment being processed
    COMPLETED,      // Payment successful
    FAILED,         // Payment failed
    REFUNDED,       // Payment refunded
    CANCELLED       // Payment cancelled
}
