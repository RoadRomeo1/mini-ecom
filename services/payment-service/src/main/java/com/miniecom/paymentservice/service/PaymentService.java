package com.miniecom.paymentservice.service;

import com.miniecom.paymentservice.dto.PaymentResponse;
import com.miniecom.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public List<PaymentResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public PaymentResponse getByOrderId(Long orderId) {
        return repo.findByOrderId(orderId).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    public List<PaymentResponse> getByUserId(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(com.miniecom.paymentservice.model.Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getUserId(),
                p.getAmount(), p.getStatus(), p.getCreatedAt());
    }
}
