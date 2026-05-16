package com.example.orderservice.event;

public record OrderCreatedEvent(
    String eventId,
    String orderId,
    String customerEmail,
    Double totalAmount,
    String timestamp
) {
    public static OrderCreatedEvent of(String orderId, String customerEmail, Double totalAmount) {
        return new OrderCreatedEvent(
            java.util.UUID.randomUUID().toString(),
            orderId,
            customerEmail,
            totalAmount,
            java.time.Instant.now().toString()
        );
    }
}