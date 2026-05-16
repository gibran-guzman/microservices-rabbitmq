package com.example.orderservice.event;

/**
 * Evento emitido cuando se crea un nuevo pedido.
 *
 * Este evento se publica en RabbitMQ a través del exchange 'order.events'
 * con routing key 'order.created' para ser consumido por notification-service.
 *
 * @param eventId Identificador único del evento (UUID)
 * @param orderId ID del pedido creado
 * @param customerEmail Email del cliente que realizó el pedido
 * @param totalAmount Monto total del pedido
 * @param timestamp Fecha y hora de creación del evento (ISO-8601)
 *
 * @see <a href="https://www.rabbitmq.com/tutorials/amqp-concepts.html#exchanges">RabbitMQ Exchanges</a>
 *
 * @example
 * <pre>
 * {
 *   "eventId": "550e8400-e29b-41d4-a716-446655440000",
 *   "orderId": "123",
 *   "customerEmail": "cliente@test.com",
 *   "totalAmount": 150.00,
 *   "timestamp": "2026-05-16T01:26:12.168Z"
 * }
 * </pre>
 */
public record OrderCreatedEvent(
    String eventId,
    String orderId,
    String customerEmail,
    Double totalAmount,
    String timestamp
) {
    /**
     * Factory method para crear una instancia de OrderCreatedEvent.
     *
     * @param orderId ID del pedido creado (no puede ser null)
     * @param customerEmail Email del cliente (no puede ser null)
     * @param totalAmount Monto total del pedido (no puede ser null)
     * @return Nueva instancia de OrderCreatedEvent con valores generados
     * @throws IllegalArgumentException si algún parámetro es null
     *
     * @example
     * <pre>
     * OrderCreatedEvent event = OrderCreatedEvent.of("123", "cliente@test.com", 150.00);
     * </pre>
     */
    public static OrderCreatedEvent of(String orderId, String customerEmail, Double totalAmount) {
        if (orderId == null || customerEmail == null || totalAmount == null) {
            throw new IllegalArgumentException("orderId, customerEmail y totalAmount son obligatorios");
        }
        return new OrderCreatedEvent(
            java.util.UUID.randomUUID().toString(),
            orderId,
            customerEmail,
            totalAmount,
            java.time.Instant.now().toString()
        );
    }
}