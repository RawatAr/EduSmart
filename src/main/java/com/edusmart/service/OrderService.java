package com.edusmart.service;

import com.edusmart.dto.order.*;
import com.edusmart.entity.*;
import com.edusmart.entity.enums.OrderStatus;
import com.edusmart.entity.enums.PaymentStatus;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for order management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    /**
     * Checkout - Convert cart to order
     */
    public OrderResponseDTO checkout(Long studentId, CheckoutRequestDTO request) {
        log.info("Checkout initiated for student: {}", studentId);

        // Get student
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Get cart
        Cart cart = cartRepository.findByStudentId(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Validate cart has items
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Check if already enrolled in any course
        for (CartItem item : cartItems) {
            if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, item.getCourse().getId())) {
                throw new BadRequestException("Already enrolled in course: " + item.getCourse().getTitle());
            }
        }

        // Create order
        Order order = Order.builder()
            .student(student)
            .orderNumber(generateOrderNumber())
            .subtotal(cart.getTotalAmount())
            .discountAmount(cart.getDiscountAmount())
            .taxAmount(0.0) // Tax can be added later
            .totalAmount(cart.getFinalAmount())
            .status(OrderStatus.PENDING)
            .notes(request.getNotes())
            .build();

        // Add coupon if applied
        if (cart.getAppliedCoupon() != null) {
            order.setAppliedCoupon(cart.getAppliedCoupon());
            order.setCouponCode(cart.getAppliedCoupon().getCode());
        }

        order = orderRepository.save(order);

        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .course(cartItem.getCourse())
                .courseTitle(cartItem.getCourse().getTitle())
                .price(cartItem.getPrice())
                .courseDescription(cartItem.getCourse().getShortDescription())
                .build();
            orderItemRepository.save(orderItem);
        }

        // Create payment record
        Payment payment = Payment.builder()
            .order(order)
            .transactionId(UUID.randomUUID().toString())
            .paymentMethod(request.getPaymentMethod())
            .status(PaymentStatus.PENDING)
            .amount(order.getTotalAmount())
            .paymentGateway("INTERNAL")
            .build();
        payment = paymentRepository.save(payment);

        // Simulate payment processing (in real app, integrate with Stripe/PayPal)
        processPayment(payment, order);

        // Clear cart after successful checkout
        cartItemRepository.deleteAll(cartItems);
        cart.setTotalAmount(0.0);
        cart.setDiscountAmount(0.0);
        cart.setFinalAmount(0.0);
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);

        log.info("Order created successfully: {}", order.getOrderNumber());
        
        // Send order confirmation email
        try {
            emailService.sendOrderConfirmationEmail(
                student.getEmail(),
                order.getOrderNumber(),
                student.getFirstName() + " " + student.getLastName(),
                order.getTotalAmount()
            );
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
        
        return buildOrderResponse(order);
    }

    /**
     * Process payment (simulate for now, integrate Stripe later)
     */
    private void processPayment(Payment payment, Order order) {
        try {
            // Simulate payment processing
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayResponse("Payment successful");
            paymentRepository.save(payment);

            // Update order status
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            // Enroll student in courses
            enrollStudentInCourses(order);

            // Mark order as completed
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            log.info("Payment processed successfully for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            log.error("Payment failed for order: {}", order.getOrderNumber(), e);
            throw new BadRequestException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Enroll student in purchased courses
     */
    private void enrollStudentInCourses(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        User student = order.getStudent();
        
        for (OrderItem item : items) {
            if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), item.getCourse().getId())) {
                Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .course(item.getCourse())
                    .progress(0)
                    .build();
                enrollmentRepository.save(enrollment);
                log.info("Student {} enrolled in course {}", student.getId(), item.getCourse().getId());
                
                // Send enrollment confirmation email
                try {
                    emailService.sendEnrollmentConfirmationEmail(
                        student.getEmail(),
                        student.getFirstName() + " " + student.getLastName(),
                        item.getCourseTitle()
                    );
                } catch (Exception e) {
                    log.error("Failed to send enrollment confirmation email", e);
                }
            }
        }
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId, Long studentId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Verify order belongs to student
        if (!order.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("Order does not belong to this student");
        }

        return buildOrderResponse(order);
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByNumber(String orderNumber, Long studentId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Verify order belongs to student
        if (!order.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("Order does not belong to this student");
        }

        return buildOrderResponse(order);
    }

    /**
     * Get student's order history
     */
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getMyOrders(Long studentId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStudentIdOrderByCreatedAtDesc(studentId, pageable);
        return orders.map(this::buildOrderResponse);
    }

    /**
     * Get orders by status (ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return orders.map(this::buildOrderResponse);
    }

    /**
     * Get total spent by student
     */
    @Transactional(readOnly = true)
    public Double getTotalSpent(Long studentId) {
        Double total = orderRepository.getTotalSpentByStudent(studentId);
        return total != null ? total : 0.0;
    }

    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + date + "-" + random;
    }

    /**
     * Build order response DTO
     */
    private OrderResponseDTO buildOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        
        List<OrderItemDTO> itemDTOs = items.stream()
            .map(item -> OrderItemDTO.builder()
                .id(item.getId())
                .courseId(item.getCourse().getId())
                .courseTitle(item.getCourseTitle())
                .courseDescription(item.getCourseDescription())
                .price(item.getPrice())
                .build())
            .collect(Collectors.toList());

        OrderResponseDTO.OrderResponseDTOBuilder builder = OrderResponseDTO.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .items(itemDTOs)
            .subtotal(order.getSubtotal())
            .discountAmount(order.getDiscountAmount())
            .taxAmount(order.getTaxAmount())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .couponCode(order.getCouponCode())
            .createdAt(order.getCreatedAt());

        // Add payment info if exists
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment != null) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .paymentGateway(payment.getPaymentGateway())
                .paidAt(payment.getPaidAt())
                .build();
            builder.payment(paymentDTO);
        }

        return builder.build();
    }
}
