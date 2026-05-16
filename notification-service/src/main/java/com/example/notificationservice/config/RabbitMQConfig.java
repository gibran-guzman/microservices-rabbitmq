package com.example.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para notification-service.
 *
 * Define la cola, exchange y binding para consumir eventos de pedidos.
 * La cola 'notification.order.created' recibe mensajes del exchange 'order.events'.
 *
 * @see <a href="https://www.rabbitmq.com/tutorials/amqp-concepts.html#queues">RabbitMQ Queues</a>
 */
@Configuration
public class RabbitMQConfig {

    /** Nombre del exchange (debe coincidir con order-service) */
    public static final String ORDER_EXCHANGE = "order.events";

    /** Routing key para eventos de creación de pedidos */
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    /** Nombre de la cola de notificaciones */
    public static final String NOTIFICATION_QUEUE = "notification.order.created";

    /**
     * Crea el exchange topic para eventos de pedidos.
     *
     * @return TopicExchange configurado
     */
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    /**
     * Crea la cola durable para notificaciones de pedidos.
     *
     * @return QueueBuilder con cola duradera
     */
    @Bean
    public Queue notificationOrderCreatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    /**
     * Binding entre la cola y el exchange con routing key específica.
     *
     * @param notificationOrderCreatedQueue Cola a bindear
     * @param orderEventsExchange Exchange destino
     * @return Binding configurado
     */
    @Bean
    public Binding binding(Queue notificationOrderCreatedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder
            .bind(notificationOrderCreatedQueue)
            .to(orderEventsExchange)
            .with(ORDER_CREATED_ROUTING_KEY);
    }

    /**
     * Configura el conversor de mensajes JSON.
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura RabbitTemplate con el converter JSON.
     *
     * @param connectionFactory Factory de conexiones RabbitMQ
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}