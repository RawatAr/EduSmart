package com.edusmart.entity.enums;

/**
 * Payment method enumeration
 */
public enum PaymentMethod {
    CREDIT_CARD,    // Credit card payment
    DEBIT_CARD,     // Debit card payment
    PAYPAL,         // PayPal payment
    STRIPE,         // Stripe payment
    BANK_TRANSFER,  // Bank transfer
    UPI,            // UPI payment (India)
    WALLET,         // Digital wallet
    FREE            // Free course (no payment)
}
