package com.miniecom.paymentservice.dto;

import com.miniecom.paymentservice.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    Long id, Long orderId, Long userId,
    BigDecimal amount, Payment.PaymentStatus status, LocalDateTime createdAt
) {}
