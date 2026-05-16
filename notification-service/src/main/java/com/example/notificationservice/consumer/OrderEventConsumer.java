package com.example.notificationservice.consumer;

import com.example.notificationservice.config.RabbitMQConfig;
import com.example.notificationservice.event.OrderCreatedEvent;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationService notificationService;

    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
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