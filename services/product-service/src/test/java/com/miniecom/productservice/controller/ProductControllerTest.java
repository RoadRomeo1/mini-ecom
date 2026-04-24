package com.miniecom.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniecom.productservice.dto.ProductRequest;
import com.miniecom.productservice.dto.ProductResponse;
import com.miniecom.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean ProductService service;

    private final ProductResponse sampleResponse =
            new ProductResponse(1L, "Laptop", "Gaming Laptop", new BigDecimal("999.99"), 50);

    @Test
    void POST_createProduct_returns201() throws Exception {
        when(service.create(any())).thenReturn(sampleResponse);
        ProductRequest req = new ProductRequest("Laptop", "Gaming Laptop", new BigDecimal("999.99"), 50);

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    void POST_createProduct_invalidBody_returns400() throws Exception {
        // missing required fields
        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_allProducts_returns200() throws Exception {
        when(service.getAll()).thenReturn(List.of(sampleResponse));

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void GET_productById_returns200() throws Exception {
        when(service.getById(1L)).thenReturn(sampleResponse);

        mvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void GET_productById_notFound_returns500() throws Exception {
        when(service.getById(99L)).thenThrow(new RuntimeException("Product not found: 99"));

        mvc.perform(get("/api/products/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void PUT_updateProduct_returns200() throws Exception {
        ProductRequest req = new ProductRequest("Laptop Pro", "Updated", new BigDecimal("1199.99"), 100);
        ProductResponse updated = new ProductResponse(1L, "Laptop Pro", "Updated", new BigDecimal("1199.99"), 100);
        when(service.update(eq(1L), any())).thenReturn(updated);

        mvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1199.99));
    }

    @Test
    void DELETE_product_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
