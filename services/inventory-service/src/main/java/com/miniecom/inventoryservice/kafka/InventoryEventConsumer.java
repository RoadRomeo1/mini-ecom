package com.miniecom.inventoryservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniecom.inventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);
    private final InventoryService inventoryService;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-placed", groupId = "inventory-service")
    public void onOrderPlaced(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            Long productId = node.get("productId").asLong();
            int quantity = node.get("quantity").asInt();
            boolean ok = inventoryService.deduct(productId, quantity);
            log.info("Inventory deduction for product {}: {}", productId, ok ? "SUCCESS" : "INSUFFICIENT");
        } catch (Exception e) {
            log.error("Failed to process order-placed event: {}", e.getMessage());
        }
    }
}
