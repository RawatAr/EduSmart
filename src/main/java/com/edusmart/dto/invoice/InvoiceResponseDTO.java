package com.edusmart.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for invoice response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {
    
    private Long id;
    private String invoiceNumber;
    private String orderNumber;
    private Long orderId;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    private String studentName;
    private String studentEmail;
    private String pdfUrl;
}
