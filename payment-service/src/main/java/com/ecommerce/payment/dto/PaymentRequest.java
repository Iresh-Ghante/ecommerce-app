package com.ecommerce.payment.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {
//    @NotNull
    private Long orderId;

//    @NotNull
    private BigDecimal amount;
}
