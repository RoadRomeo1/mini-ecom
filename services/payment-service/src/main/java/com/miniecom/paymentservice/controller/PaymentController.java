package com.miniecom.paymentservice.controller;

import com.miniecom.paymentservice.dto.PaymentResponse;
import com.miniecom.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getByOrderId(@PathVariable Long orderId) {
        return service.getByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentResponse> getByUserId(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }
}
