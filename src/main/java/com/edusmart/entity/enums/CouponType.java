package com.edusmart.entity.enums;

/**
 * Coupon type enumeration
 */
public enum CouponType {
    GENERAL,            // Platform-wide
    INSTRUCTOR_SPECIFIC, // Instructor's courses only
    COURSE_SPECIFIC,     // Specific course only
    FIRST_PURCHASE,      // First-time buyers
    SEASONAL            // Holiday/seasonal sales
}
