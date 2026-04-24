package com.miniecom.paymentservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniecom.paymentservice.model.Payment;
import com.miniecom.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final PaymentRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentEventConsumer(PaymentRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "order-placed", groupId = "payment-service")
    public void onOrderPlaced(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            Long orderId = node.get("orderId").asLong();
            Long userId = node.get("userId").asLong();
            BigDecimal amount = new BigDecimal(node.get("quantity").asText()); // placeholder

            Payment payment = new Payment(orderId, userId, amount);
            payment.setStatus(Payment.PaymentStatus.SUCCESS); // auto-approve for now
            repo.save(payment);
            log.info("Payment processed for order {}: SUCCESS", orderId);
        } catch (Exception e) {
            log.error("Failed to process payment event: {}", e.getMessage());
        }
    }
}
