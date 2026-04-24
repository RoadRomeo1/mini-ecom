package com.miniecom.orderservice.service;

import com.miniecom.orderservice.dto.OrderRequest;
import com.miniecom.orderservice.dto.OrderResponse;
import com.miniecom.orderservice.kafka.OrderEventProducer;
import com.miniecom.orderservice.model.Order;
import com.miniecom.orderservice.repository.OrderRepository;
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
class OrderServiceTest {

    @Mock OrderRepository repo;
    @Mock OrderEventProducer producer;
    @InjectMocks OrderService service;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order(1L, 1L, 3, new BigDecimal("2999.97"));
        try {
            var f = Order.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(order, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void place_savesOrderAndPublishesEvent() {
        when(repo.save(any())).thenReturn(order);
        OrderRequest req = new OrderRequest(1L, 1L, 3, new BigDecimal("2999.97"));

        OrderResponse res = service.place(req);

        assertThat(res.userId()).isEqualTo(1L);
        assertThat(res.productId()).isEqualTo(1L);
        assertThat(res.quantity()).isEqualTo(3);
        assertThat(res.status()).isEqualTo(Order.OrderStatus.PENDING);
        verify(repo).save(any());
        verify(producer).publishOrderPlaced(any(), any(), anyInt(), any());
    }

    @Test
    void getAll_returnsAllOrders() {
        when(repo.findAll()).thenReturn(List.of(order));

        List<OrderResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).quantity()).isEqualTo(3);
    }

    @Test
    void getById_existingId_returnsOrder() {
        when(repo.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse res = service.getById(1L);

        assertThat(res.id()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found: 99");
    }

    @Test
    void getByUser_returnsUserOrders() {
        when(repo.findByUserId(1L)).thenReturn(List.of(order));

        List<OrderResponse> result = service.getByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(1L);
    }
}
