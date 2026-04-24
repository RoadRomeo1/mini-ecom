package com.miniecom.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
    @NotNull Long userId,
    @NotNull Long productId,
    @NotNull @Positive Integer quantity,
    @NotNull @Positive java.math.BigDecimal totalPrice
) {}
