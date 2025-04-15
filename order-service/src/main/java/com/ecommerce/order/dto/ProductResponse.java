package com.ecommerce.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse implements Serializable{
   
	private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}

