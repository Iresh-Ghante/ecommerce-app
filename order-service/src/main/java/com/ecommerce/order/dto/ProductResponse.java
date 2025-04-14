package com.ecommerce.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductResponse implements Serializable{
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}

