package com.ecommerce.product.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.product.model.Product;

@Service
public class ProductEventPublish {

	private static final String PRODUCT_TOPIC = "product-events";

	@Autowired
	private KafkaTemplate<String, Product> kafkaTemplate;

	public void publishProductCreated(Product product) {
		kafkaTemplate.send(PRODUCT_TOPIC, product);
	}

	public void publishProductUpdated(Product product) {
		kafkaTemplate.send(PRODUCT_TOPIC, product);
	}
}
