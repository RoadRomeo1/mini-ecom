package com.miniecom.paymentservice.service;

import com.miniecom.paymentservice.dto.PaymentResponse;
import com.miniecom.paymentservice.model.Payment;
import com.miniecom.paymentservice.repository.PaymentRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository repo;
    @InjectMocks PaymentService service;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(1L, 1L, new BigDecimal("2999.97"));
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        try {
            var f = Payment.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(payment, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void getAll_returnsAllPayments() {
        when(repo.findAll()).thenReturn(List.of(payment));

        List<PaymentResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(Payment.PaymentStatus.SUCCESS);
    }

    @Test
    void getByOrderId_existing_returnsPayment() {
        when(repo.findByOrderId(1L)).thenReturn(Optional.of(payment));

        PaymentResponse res = service.getByOrderId(1L);

        assertThat(res.orderId()).isEqualTo(1L);
        assertThat(res.status()).isEqualTo(Payment.PaymentStatus.SUCCESS);
    }

    @Test
    void getByOrderId_notFound_throwsException() {
        when(repo.findByOrderId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByOrderId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Payment not found for order: 99");
    }

    @Test
    void getByUserId_returnsUserPayments() {
        when(repo.findByUserId(1L)).thenReturn(List.of(payment));

        List<PaymentResponse> result = service.getByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(1L);
    }
}
