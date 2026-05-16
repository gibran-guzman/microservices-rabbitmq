package com.example.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.events";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String NOTIFICATION_QUEUE = "notification.order.created";

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue notificationOrderCreatedQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding binding(Queue notificationOrderCreatedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder
            .bind(notificationOrderCreatedQueue)
            .to(orderEventsExchange)
            .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}