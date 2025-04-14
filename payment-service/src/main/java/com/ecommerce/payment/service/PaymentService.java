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

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request, String userEmail) {
        // Simulate a successful payment for now (mocking payment gateway)
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userEmail(userEmail)
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    public List<PaymentResponse> getUserPayments(String userEmail) {
        return paymentRepository.findByUserEmail(userEmail).stream()
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
