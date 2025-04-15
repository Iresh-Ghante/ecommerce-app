package com.ecommerce.product.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.product.model.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductEventPublish {

	private static final String PRODUCT_TOPIC = "product-events";

	@Autowired
	private KafkaTemplate<String, Product> kafkaTemplate;

	public void publishProductCreated(Product product) {
		try {
			kafkaTemplate.send(PRODUCT_TOPIC, product);
			log.info("Published product created event: {}", product.getId());
		} catch (Exception e) {
			log.error("Failed to publish product created event: {}", product.getId(), e);
		}
	}

	public void publishProductUpdated(Product product) {
		try {
			kafkaTemplate.send(PRODUCT_TOPIC, product);
			log.info("Published product updated event: {}", product.getId());
		} catch (Exception e) {
			log.error("Failed to publish product updated event: {}", product.getId(), e);
		}
	}
}
