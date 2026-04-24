package com.miniecom.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniecom.userservice.dto.UserRequest;
import com.miniecom.userservice.dto.UserResponse;
import com.miniecom.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean UserService service;

    private final UserResponse sample = new UserResponse(1L, "John Doe", "john@example.com", "9999999999");

    @Test
    void POST_createUser_returns201() throws Exception {
        when(service.create(any())).thenReturn(sample);

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new UserRequest("John Doe", "john@example.com", "9999999999"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void POST_createUser_invalidEmail_returns400() throws Exception {
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John\",\"email\":\"not-an-email\",\"phone\":\"999\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void POST_createUser_missingFields_returns400() throws Exception {
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_allUsers_returns200() throws Exception {
        when(service.getAll()).thenReturn(List.of(sample));

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void GET_userById_returns200() throws Exception {
        when(service.getById(1L)).thenReturn(sample);

        mvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void PUT_updateUser_returns200() throws Exception {
        UserResponse updated = new UserResponse(1L, "John Updated", "john@example.com", "1111111111");
        when(service.update(eq(1L), any())).thenReturn(updated);

        mvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new UserRequest("John Updated", "john@example.com", "1111111111"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    void DELETE_user_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
