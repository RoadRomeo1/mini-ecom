package com.miniecom.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniecom.orderservice.dto.OrderRequest;
import com.miniecom.orderservice.dto.OrderResponse;
import com.miniecom.orderservice.model.Order;
import com.miniecom.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean OrderService service;

    private final OrderResponse sample = new OrderResponse(
            1L, 1L, 1L, 3, new BigDecimal("2999.97"),
            Order.OrderStatus.PENDING, LocalDateTime.now());

    @Test
    void POST_placeOrder_returns201() throws Exception {
        when(service.place(any())).thenReturn(sample);
        OrderRequest req = new OrderRequest(1L, 1L, 3, new BigDecimal("2999.97"));

        mvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void POST_placeOrder_missingFields_returns400() throws Exception {
        mvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_allOrders_returns200() throws Exception {
        when(service.getAll()).thenReturn(List.of(sample));

        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void GET_orderById_returns200() throws Exception {
        when(service.getById(1L)).thenReturn(sample);

        mvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void GET_ordersByUser_returns200() throws Exception {
        when(service.getByUser(1L)).thenReturn(List.of(sample));

        mvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
