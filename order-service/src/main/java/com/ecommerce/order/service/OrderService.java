package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderPlacedEvent;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderResponse placeOrder(OrderRequest request, String userEmail) {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            ProductResponse product = productClient.getProductById(itemReq.getProductId());

            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemTotal);

            items.add(OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .price(product.getPrice())
                    .build()
            );
        }

        Order order = Order.builder()
                .userEmail(userEmail)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .totalAmount(total)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order)); // assign parent
        Order saved = orderRepository.save(order);

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(saved.getId())
                .userEmail(userEmail)
                .totalAmount(total)
                .orderDate(saved.getOrderDate())
                .build();

        kafkaTemplate.send("order-placed-topic", event);
        
        return mapToResponse(saved);
    }

    public List<OrderResponse> getOrdersForUser(String userEmail) {
        return orderRepository.findByUserEmail(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userEmail(order.getUserEmail())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

