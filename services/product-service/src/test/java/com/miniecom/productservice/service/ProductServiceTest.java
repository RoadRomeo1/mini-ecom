package com.miniecom.productservice.service;

import com.miniecom.productservice.dto.ProductRequest;
import com.miniecom.productservice.dto.ProductResponse;
import com.miniecom.productservice.model.Product;
import com.miniecom.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository repo;
    @InjectMocks ProductService service;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Laptop", "Gaming Laptop", new BigDecimal("999.99"), 50);
        // simulate persisted id via reflection
        try {
            var f = Product.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(product, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void create_savesAndReturnsProduct() {
        when(repo.save(any())).thenReturn(product);
        ProductRequest req = new ProductRequest("Laptop", "Gaming Laptop", new BigDecimal("999.99"), 50);

        ProductResponse res = service.create(req);

        assertThat(res.name()).isEqualTo("Laptop");
        assertThat(res.price()).isEqualByComparingTo("999.99");
        assertThat(res.stock()).isEqualTo(50);
        verify(repo).save(any());
    }

    @Test
    void getAll_returnsAllProducts() {
        when(repo.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Laptop");
    }

    @Test
    void getById_existingId_returnsProduct() {
        when(repo.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse res = service.getById(1L);

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("Laptop");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found: 99");
    }

    @Test
    void update_existingProduct_updatesFields() {
        when(repo.findById(1L)).thenReturn(Optional.of(product));
        when(repo.save(any())).thenReturn(product);
        ProductRequest req = new ProductRequest("Laptop Pro", "Updated", new BigDecimal("1199.99"), 100);

        ProductResponse res = service.update(1L, req);

        assertThat(res.name()).isEqualTo("Laptop Pro");
        verify(repo).save(product);
    }

    @Test
    void update_nonExistingProduct_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        ProductRequest req = new ProductRequest("X", "X", BigDecimal.ONE, 1);

        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found: 99");
    }

    @Test
    void delete_callsRepository() {
        service.delete(1L);
        verify(repo).deleteById(1L);
    }
}
