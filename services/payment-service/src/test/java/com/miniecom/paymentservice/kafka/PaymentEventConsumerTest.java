package com.miniecom.paymentservice.kafka;

import com.miniecom.paymentservice.model.Payment;
import com.miniecom.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock PaymentRepository repo;
    @InjectMocks PaymentEventConsumer consumer;

    @Test
    void onOrderPlaced_validMessage_savesPaymentAsSuccess() {
        consumer.onOrderPlaced("{\"orderId\":1,\"productId\":1,\"quantity\":3,\"userId\":1}");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(repo).save(captor.capture());

        Payment saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getStatus()).isEqualTo(Payment.PaymentStatus.SUCCESS);
    }

    @Test
    void onOrderPlaced_malformedJson_doesNotThrow() {
        consumer.onOrderPlaced("bad-json");

        verifyNoInteractions(repo);
    }
}
