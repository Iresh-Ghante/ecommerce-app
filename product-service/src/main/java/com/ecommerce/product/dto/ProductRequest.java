package com.ecommerce.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    private Integer stock;

    private String category;

    private String imageUrl;
}
