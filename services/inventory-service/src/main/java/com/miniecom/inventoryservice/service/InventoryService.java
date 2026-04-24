package com.miniecom.inventoryservice.service;

import com.miniecom.inventoryservice.dto.InventoryRequest;
import com.miniecom.inventoryservice.dto.InventoryResponse;
import com.miniecom.inventoryservice.model.Inventory;
import com.miniecom.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    public InventoryResponse upsert(InventoryRequest req) {
        Inventory inv = repo.findByProductId(req.productId())
                .orElse(new Inventory(req.productId(), 0));
        inv.setQuantity(req.quantity());
        return toResponse(repo.save(inv));
    }

    public InventoryResponse getByProductId(Long productId) {
        return repo.findByProductId(productId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
    }

    public List<InventoryResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    // Called by Kafka consumer when an order is placed
    public boolean deduct(Long productId, int qty) {
        Inventory inv = repo.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        if (inv.getQuantity() < qty) return false;
        inv.setQuantity(inv.getQuantity() - qty);
        repo.save(inv);
        return true;
    }

    private InventoryResponse toResponse(Inventory i) {
        return new InventoryResponse(i.getId(), i.getProductId(), i.getQuantity());
    }
}
