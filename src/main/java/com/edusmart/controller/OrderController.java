package com.edusmart.controller;

import com.edusmart.dto.order.CheckoutRequestDTO;
import com.edusmart.dto.order.OrderResponseDTO;
import com.edusmart.entity.enums.OrderStatus;
import com.edusmart.security.UserPrincipal;
import com.edusmart.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order operations
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Checkout - Convert cart to order
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<OrderResponseDTO> checkout(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CheckoutRequestDTO request) {
        OrderResponseDTO response = orderService.checkout(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long orderId) {
        OrderResponseDTO response = orderService.getOrderById(orderId, currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<OrderResponseDTO> getOrderByNumber(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderNumber) {
        OrderResponseDTO response = orderService.getOrderByNumber(orderNumber, currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get my order history
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponseDTO> orders = orderService.getMyOrders(currentUser.getId(), pageRequest);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get total amount spent
     */
    @GetMapping("/total-spent")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Double> getTotalSpent(@AuthenticationPrincipal UserPrincipal currentUser) {
        Double total = orderService.getTotalSpent(currentUser.getId());
        return ResponseEntity.ok(total);
    }
    
    /**
     * Get orders by status (ADMIN only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponseDTO> orders = orderService.getOrdersByStatus(status, pageRequest);
        return ResponseEntity.ok(orders);
    }
}
