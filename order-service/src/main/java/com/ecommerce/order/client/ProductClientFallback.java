
package com.ecommerce.order.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ecommerce.order.dto.ProductResponse;

@Component
public class ProductClientFallback implements ProductClient {

	@Override
	public ProductResponse getProductById(Long id) {
		// Fallback logic when the product service is unavailable
		return ProductResponse.builder()
				.id(id)
				.description("Product information not available")
				.stock(0)
				.name("Unavailable")
				.price(BigDecimal.ZERO)
				.build();
	}
}