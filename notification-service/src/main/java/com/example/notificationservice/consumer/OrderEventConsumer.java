package com.example.notificationservice.consumer;

import com.example.notificationservice.config.RabbitMQConfig;
import com.example.notificationservice.event.OrderCreatedEvent;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de pedidos desde RabbitMQ.
 *
 * Escucha la cola 'notification.order.created' y procesa eventos de tipo
 * OrderCreatedEvent, persistiendo una notificación en la base de datos.
 *
 * @see RabbitMQConfig#NOTIFICATION_QUEUE
 * @see OrderCreatedEvent
 */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationService notificationService;

    /**
     * Constructor con dependencias.
     *
     * @param notificationService Servicio de notificaciones
     */
    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Procesa eventos de creación de pedidos.
     *
     * Este método es invocado automáticamente por Spring AMQP cuando llega
     * un mensaje a la cola configurada. Deserializa el JSON a OrderCreatedEvent
     * y registra una notificación en la base de datos.
     *
     * @param event Evento de pedido creado (no puede ser null)
     * @throws IllegalArgumentException si el evento es null o tiene datos inválidos
     * @throws org.springframework.amqp.AmqpException si falla el procesamiento
     *
     * @example
     * <pre>
     * // Mensaje recibido en cola:
     * {
     *   "eventId": "550e8400-e29b-41d4-a716-446655440000",
     *   "orderId": "123",
     *   "customerEmail": "cliente@test.com",
     *   "totalAmount": 150.00,
     *   "timestamp": "2026-05-16T01:26:12.168Z"
     * }
     * </pre>
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        log.info("Received order.created event: orderId={}, email={}",
            event.orderId(), event.customerEmail());

        notificationService.processNotification(
            event.eventId(),
            event.orderId(),
            event.customerEmail()
        );

        log.info("Notification processed for order: {}", event.orderId());
    }
}