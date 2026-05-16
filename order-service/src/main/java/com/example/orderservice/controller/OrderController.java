package com.example.orderservice.controller;

import com.example.orderservice.config.RabbitMQConfig;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderController(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setEmail(request.getEmail());
        order.setTotal(request.getTotal());
        order.setCurrency(request.getCurrency());

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.of(
            savedOrder.getId().toString(),
            savedOrder.getEmail(),
            savedOrder.getTotal().doubleValue()
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
            event
        );

        log.info("Published order.created event for order: {}", savedOrder.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(OrderResponse.fromEntity(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}