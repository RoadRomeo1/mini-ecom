package com.miniecom.inventoryservice.service;

import com.miniecom.inventoryservice.dto.InventoryRequest;
import com.miniecom.inventoryservice.dto.InventoryResponse;
import com.miniecom.inventoryservice.model.Inventory;
import com.miniecom.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock InventoryRepository repo;
    @InjectMocks InventoryService service;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(1L, 50);
    }

    @Test
    void upsert_newProduct_createsInventory() {
        when(repo.findByProductId(1L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenReturn(inventory);

        InventoryResponse res = service.upsert(new InventoryRequest(1L, 50));

        assertThat(res.productId()).isEqualTo(1L);
        assertThat(res.quantity()).isEqualTo(50);
        verify(repo).save(any());
    }

    @Test
    void upsert_existingProduct_updatesQuantity() {
        when(repo.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(repo.save(any())).thenReturn(inventory);

        InventoryResponse res = service.upsert(new InventoryRequest(1L, 100));

        assertThat(res.quantity()).isEqualTo(100);
    }

    @Test
    void getByProductId_existing_returnsInventory() {
        when(repo.findByProductId(1L)).thenReturn(Optional.of(inventory));

        InventoryResponse res = service.getByProductId(1L);

        assertThat(res.productId()).isEqualTo(1L);
        assertThat(res.quantity()).isEqualTo(50);
    }

    @Test
    void getByProductId_notFound_throwsException() {
        when(repo.findByProductId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByProductId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Inventory not found for product: 99");
    }

    @Test
    void deduct_sufficientStock_reducesQuantityAndReturnsTrue() {
        when(repo.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(repo.save(any())).thenReturn(inventory);

        boolean result = service.deduct(1L, 10);

        assertThat(result).isTrue();
        assertThat(inventory.getQuantity()).isEqualTo(40);
        verify(repo).save(inventory);
    }

    @Test
    void deduct_insufficientStock_returnsFalseWithoutSaving() {
        when(repo.findByProductId(1L)).thenReturn(Optional.of(inventory));

        boolean result = service.deduct(1L, 100);

        assertThat(result).isFalse();
        verify(repo, never()).save(any());
    }

    @Test
    void deduct_exactStock_reducesToZeroAndReturnsTrue() {
        when(repo.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(repo.save(any())).thenReturn(inventory);

        boolean result = service.deduct(1L, 50);

        assertThat(result).isTrue();
        assertThat(inventory.getQuantity()).isEqualTo(0);
    }

    @Test
    void getAll_returnsAllRecords() {
        when(repo.findAll()).thenReturn(List.of(inventory));

        List<InventoryResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }
}
