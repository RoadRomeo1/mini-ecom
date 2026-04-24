package com.miniecom.orderservice.dto;

import com.miniecom.orderservice.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id, Long userId, Long productId,
    Integer quantity, BigDecimal totalPrice,
    Order.OrderStatus status, LocalDateTime createdAt
) {}
