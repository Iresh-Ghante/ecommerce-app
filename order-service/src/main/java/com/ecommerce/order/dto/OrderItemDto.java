package com.ecommerce.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
