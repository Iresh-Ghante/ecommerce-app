package com.ecommerce.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ecommerce.payment.model.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}
