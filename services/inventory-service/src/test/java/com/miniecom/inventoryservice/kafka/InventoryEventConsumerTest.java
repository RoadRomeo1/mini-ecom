package com.miniecom.inventoryservice.kafka;

import com.miniecom.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventConsumerTest {

    @Mock InventoryService inventoryService;
    @InjectMocks InventoryEventConsumer consumer;

    @Test
    void onOrderPlaced_validMessage_deductsInventory() {
        when(inventoryService.deduct(1L, 3)).thenReturn(true);

        consumer.onOrderPlaced("{\"orderId\":1,\"productId\":1,\"quantity\":3,\"userId\":1}");

        verify(inventoryService).deduct(1L, 3);
    }

    @Test
    void onOrderPlaced_insufficientStock_logsInsufficient() {
        when(inventoryService.deduct(1L, 100)).thenReturn(false);

        consumer.onOrderPlaced("{\"orderId\":1,\"productId\":1,\"quantity\":100,\"userId\":1}");

        verify(inventoryService).deduct(1L, 100);
    }

    @Test
    void onOrderPlaced_malformedJson_doesNotThrow() {
        // should swallow the error gracefully
        consumer.onOrderPlaced("not-valid-json");

        verifyNoInteractions(inventoryService);
    }
}
