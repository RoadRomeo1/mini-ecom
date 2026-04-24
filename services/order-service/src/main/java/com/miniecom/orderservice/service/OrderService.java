package com.miniecom.orderservice.service;

import com.miniecom.orderservice.dto.OrderRequest;
import com.miniecom.orderservice.dto.OrderResponse;
import com.miniecom.orderservice.kafka.OrderEventProducer;
import com.miniecom.orderservice.model.Order;
import com.miniecom.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final OrderEventProducer producer;

    public OrderService(OrderRepository repo, OrderEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    public OrderResponse place(OrderRequest req) {
        Order order = new Order(req.userId(), req.productId(), req.quantity(), req.totalPrice());
        order = repo.save(order);
        producer.publishOrderPlaced(order.getId(), order.getProductId(), order.getQuantity(), order.getUserId());
        return toResponse(order);
    }

    public List<OrderResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public OrderResponse getById(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<OrderResponse> getByUser(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order o) {
        return new OrderResponse(o.getId(), o.getUserId(), o.getProductId(),
                o.getQuantity(), o.getTotalPrice(), o.getStatus(), o.getCreatedAt());
    }
}
