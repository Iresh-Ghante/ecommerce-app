package com.ecommerce.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestBody @Validated OrderRequest request) {
        String userEmail = jwt.getSubject();
        log.info("Placing order for user: {}", userEmail);
        try {
            return ResponseEntity.ok(orderService.placeOrder(request, userEmail));
        } catch (Exception e) {
            log.error("Error placing order for user: {}", userEmail, e);
            return ResponseEntity.status(500).body(null); // You may want to provide a better error response.
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getSubject();
        log.info("Fetching orders for user: {}", userEmail);
        List<OrderResponse> orders = orderService.getOrdersForUser(userEmail);
        return ResponseEntity.ok(orders);
    }
}
