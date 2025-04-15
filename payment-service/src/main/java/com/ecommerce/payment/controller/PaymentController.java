package com.ecommerce.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody PaymentRequest request
    ) {
        String userEmail = jwt.getSubject();
        log.info("Processing payment for user: {}, orderId: {}", userEmail, request.getOrderId());
        PaymentResponse response = paymentService.processPayment(request, userEmail);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getSubject();
        log.info("Fetching payments for user: {}", userEmail);
        return ResponseEntity.ok(paymentService.getUserPayments(userEmail));
    }
}
