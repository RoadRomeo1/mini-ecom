package com.miniecom.orderservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order-placed";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlaced(Long orderId, Long productId, int quantity, Long userId) {
        String payload = String.format(
            "{\"orderId\":%d,\"productId\":%d,\"quantity\":%d,\"userId\":%d}",
            orderId, productId, quantity, userId
        );
        kafkaTemplate.send(TOPIC, payload);
        log.info("Published order-placed event: {}", payload);
    }
}
