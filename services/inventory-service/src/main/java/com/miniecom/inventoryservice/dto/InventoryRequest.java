package com.miniecom.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequest(@NotNull Long productId, @NotNull @PositiveOrZero Integer quantity) {}
