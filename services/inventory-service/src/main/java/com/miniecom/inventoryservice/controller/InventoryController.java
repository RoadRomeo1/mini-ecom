package com.miniecom.inventoryservice.controller;

import com.miniecom.inventoryservice.dto.InventoryRequest;
import com.miniecom.inventoryservice.dto.InventoryResponse;
import com.miniecom.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping
    public InventoryResponse upsert(@Valid @RequestBody InventoryRequest req) {
        return service.upsert(req);
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/product/{productId}")
    public InventoryResponse getByProductId(@PathVariable Long productId) {
        return service.getByProductId(productId);
    }
}
