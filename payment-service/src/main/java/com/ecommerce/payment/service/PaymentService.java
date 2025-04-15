package com.ecommerce.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request, String userEmail) {
        log.info("Processing payment for orderId={}, user={}", request.getOrderId(), userEmail);

        // Simulate successful payment (integration with actual gateway comes later)
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userEmail(userEmail)
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment processed successfully. PaymentId={}, status={}", saved.getId(), saved.getStatus());

        return mapToResponse(saved);
    }

    public List<PaymentResponse> getUserPayments(String userEmail) {
        log.info("Fetching payment history for user={}", userEmail);

        List<Payment> payments = paymentRepository.findByUserEmail(userEmail);

        if (payments.isEmpty()) {
            throw new RuntimeException("No payments found for user: " + userEmail);
        }

        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
