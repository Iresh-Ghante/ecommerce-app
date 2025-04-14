package com.ecommerce.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
}
