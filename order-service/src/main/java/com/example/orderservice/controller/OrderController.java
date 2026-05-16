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

/**
 * Controlador REST para operaciones de pedidos.
 *
 * Maneja la creación de pedidos y publicación de eventos a RabbitMQ.
 * Endpoints: POST /orders, GET /orders/{id}
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor con dependencias.
     *
     * @param orderRepository Repositorio de pedidos
     * @param rabbitTemplate Template para envío de mensajes RabbitMQ
     */
    public OrderController(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Crea un nuevo pedido y emite evento a RabbitMQ.
     *
     * @param request Datos del pedido a crear (validado)
     * @return ResponseEntity con el pedido creado (HTTP 201)
     * @throws org.springframework.web.bind.MethodArgumentNotValidException si la validación falla
     * @throws org.springframework.amqp.AmqpException si falla el envío del evento
     *
     * @apiEndpoint POST /orders
     * @apiDescription Crea un nuevo pedido y publica evento order.created
     *
     * @example
     * <pre>
     * curl -X POST http://localhost:8081/orders \
     *   -H "Content-Type: application/json" \
     *   -d '{"customerId": "cust-001", "email": "test@test.com", "total": 100.00, "currency": "USD"}'
     * </pre>
     */
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

    /**
     * Obtiene un pedido por su ID.
     *
     * @param id ID del pedido a buscar
     * @return ResponseEntity con el pedido (HTTP 200) o not found (HTTP 404)
     * @throws IllegalArgumentException si id es null
     *
     * @apiEndpoint GET /orders/{id}
     * @apiDescription Retorna un pedido existente por su ID
     *
     * @example
     * <pre>
     * curl http://localhost:8081/orders/1
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(OrderResponse.fromEntity(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}