package com.ecommerce.payment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.payment.dto.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderEventsListener {

    @KafkaListener(topics = "order-placed-topic", groupId = "payment-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order event: {}", event);

        // Optional: trigger payment flow based on this event
    }
}
