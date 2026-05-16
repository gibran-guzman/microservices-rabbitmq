package com.example.orderservice;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createOrder_validRequest_returnsCreated() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId("CUST-001");
        request.setEmail("test@example.com");
        request.setTotal(new BigDecimal("100.50"));
        request.setCurrency("USD");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.total").value(100.50))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void createOrder_invalidEmail_returnsBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId("CUST-001");
        request.setEmail("invalid-email");
        request.setTotal(new BigDecimal("100.50"));
        request.setCurrency("USD");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_missingRequiredFields_returnsBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_existingOrder_returnsOrder() throws Exception {
        Order order = new Order();
        order.setCustomerId("CUST-002");
        order.setEmail("test2@example.com");
        order.setTotal(new BigDecimal("200.00"));
        order.setCurrency("EUR");
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.customerId").value("CUST-002"))
                .andExpect(jsonPath("$.email").value("test2@example.com"))
                .andExpect(jsonPath("$.total").value(200.00))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void getOrder_nonExistingOrder_returnsNotFound() throws Exception {
        mockMvc.perform(get("/orders/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_persistsToDatabase() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId("CUST-003");
        request.setEmail("persist@example.com");
        request.setTotal(new BigDecimal("150.75"));
        request.setCurrency("GBP");

        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        OrderResponse orderResponse = objectMapper.readValue(response, OrderResponse.class);
        Optional<Order> found = orderRepository.findById(orderResponse.getId());

        assertTrue(found.isPresent());
        assertEquals("CUST-003", found.get().getCustomerId());
        assertEquals("persist@example.com", found.get().getEmail());
        assertEquals(new BigDecimal("150.75"), found.get().getTotal());
        assertEquals("GBP", found.get().getCurrency());
    }
}