package com.edusmart.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for revenue reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueReportDTO {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private Double averageOrderValue;
    private Long totalOrders;
    private Long totalCustomers;
    private Map<String, Double> revenueByDay;
    private Map<String, Double> revenueByCategory;
}
