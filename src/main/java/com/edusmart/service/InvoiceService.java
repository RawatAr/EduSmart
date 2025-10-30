package com.edusmart.service;

import com.edusmart.dto.invoice.InvoiceResponseDTO;
import com.edusmart.entity.Invoice;
import com.edusmart.entity.Order;
import com.edusmart.entity.User;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.InvoiceRepository;
import com.edusmart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for invoice management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    /**
     * Generate invoice for order
     */
    public InvoiceResponseDTO generateInvoice(Long orderId) {
        log.info("Generating invoice for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Check if invoice already exists
        if (invoiceRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalStateException("Invoice already exists for this order");
        }

        // Create invoice
        Invoice invoice = Invoice.builder()
            .order(order)
            .invoiceNumber(generateInvoiceNumber())
            .invoiceDate(LocalDateTime.now())
            .dueDate(LocalDateTime.now().plusDays(30))
            .subtotal(order.getSubtotal())
            .discountAmount(order.getDiscountAmount())
            .taxAmount(order.getTaxAmount())
            .totalAmount(order.getTotalAmount())
            .isPaid(true)
            .paidAt(LocalDateTime.now())
            .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice generated: {}", invoice.getInvoiceNumber());

        return mapToDTO(invoice);
    }

    /**
     * Get invoice by ID
     */
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return mapToDTO(invoice);
    }

    /**
     * Get invoice by invoice number
     */
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return mapToDTO(invoice);
    }

    /**
     * Get invoice by order ID
     */
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByOrderId(Long orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for this order"));
        return mapToDTO(invoice);
    }

    /**
     * Get student invoices
     */
    @Transactional(readOnly = true)
    public Page<InvoiceResponseDTO> getStudentInvoices(Long studentId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByStudentId(studentId, pageable);
        return invoices.map(this::mapToDTO);
    }

    /**
     * Get all invoices (ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<InvoiceResponseDTO> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return invoices.map(this::mapToDTO);
    }

    /**
     * Download invoice PDF (placeholder for PDF generation)
     */
    public byte[] downloadInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // TODO: Implement PDF generation using libraries like iText or Apache PDFBox
        // For now, return placeholder
        log.info("PDF download requested for invoice: {}", invoice.getInvoiceNumber());
        
        String pdfContent = String.format(
            "INVOICE\n\nInvoice Number: %s\nDate: %s\n\nTotal: $%.2f\n\nThank you for your purchase!",
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate(),
            invoice.getTotalAmount()
        );
        
        return pdfContent.getBytes();
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "INV-" + date + "-" + random;
    }

    /**
     * Map to DTO
     */
    private InvoiceResponseDTO mapToDTO(Invoice invoice) {
        Order order = invoice.getOrder();
        User student = order.getStudent();

        return InvoiceResponseDTO.builder()
            .id(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .orderNumber(order.getOrderNumber())
            .orderId(order.getId())
            .invoiceDate(invoice.getInvoiceDate())
            .dueDate(invoice.getDueDate())
            .subtotal(invoice.getSubtotal())
            .discountAmount(invoice.getDiscountAmount())
            .taxAmount(invoice.getTaxAmount())
            .totalAmount(invoice.getTotalAmount())
            .isPaid(invoice.getIsPaid())
            .paidAt(invoice.getPaidAt())
            .studentName(student.getFirstName() + " " + student.getLastName())
            .studentEmail(student.getEmail())
            .pdfUrl("/api/invoices/" + invoice.getId() + "/pdf")
            .build();
    }
}
