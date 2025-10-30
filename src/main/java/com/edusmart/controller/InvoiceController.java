package com.edusmart.controller;

import com.edusmart.dto.invoice.InvoiceResponseDTO;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for invoice operations
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    /**
     * Get invoice by ID
     */
    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable Long invoiceId) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoice);
    }
    
    /**
     * Get invoice by invoice number
     */
    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }
    
    /**
     * Get invoice by order ID
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByOrderId(@PathVariable Long orderId) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }
    
    /**
     * Get my invoices
     */
    @GetMapping("/my-invoices")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<InvoiceResponseDTO>> getMyInvoices(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<InvoiceResponseDTO> invoices = invoiceService.getStudentInvoices(currentUser.getId(), pageRequest);
        return ResponseEntity.ok(invoices);
    }
    
    /**
     * Get all invoices (ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvoiceResponseDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices(pageRequest);
        return ResponseEntity.ok(invoices);
    }
    
    /**
     * Download invoice PDF
     */
    @GetMapping("/{invoiceId}/pdf")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long invoiceId) {
        byte[] pdfData = invoiceService.downloadInvoicePdf(invoiceId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoiceId + ".pdf");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfData);
    }
}
