package com.example.orderservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para order-service.
 *
 * Define el exchange tipo topic para publicación de eventos de pedidos.
 * Utiliza Jackson para serialización JSON de mensajes.
 *
 * @see <a href="https://docs.spring.io/spring-amqp/reference/">Spring AMQP</a>
 */
@Configuration
public class RabbitMQConfig {

    /** Nombre del exchange para eventos de pedidos */
    public static final String ORDER_EXCHANGE = "order.events";

    /** Routing key para eventos de creación de pedidos */
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    /**
     * Crea el exchange topic para eventos de pedidos.
     *
     * @return TopicExchange configurado con nombre 'order.events'
     */
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    /**
     * Configura el conversor de mensajes a JSON usando Jackson.
     *
     * @return Jackson2JsonMessageConverter para serialización/deserialización
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura RabbitTemplate con el converter JSON.
     *
     * @param connectionFactory Factory de conexiones RabbitMQ
     * @return RabbitTemplate configurado para envío de mensajes JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}