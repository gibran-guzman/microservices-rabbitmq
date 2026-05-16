package com.example.notificationservice.event;

public record OrderCreatedEvent(
    String eventId,
    String orderId,
    String customerEmail,
    Double totalAmount,
    String timestamp
) {}