# notification-service

Microservicio de notificaciones. Consume eventos `order.created` desde RabbitMQ y persiste registros.

## Endpoints

### POST /notifications

Crea una notificación manualmente (uso interno para testing).

**Parámetros Query:**
- `eventId` (string, requerido): Identificador del evento
- `orderId` (string, requerido): ID del pedido
- `email` (string, requerido): Email del destinatario

**Ejemplo:**
```bash
curl -X POST "http://localhost:8082/notifications?eventId=evt-001&orderId=ord-123&email=test@example.com"
```

**Respuesta (200):**
```json
{
  "id": 1,
  "eventId": "evt-001",
  "orderId": "ord-123",
  "email": "test@example.com",
  "status": "SENT",
  "processedAt": "2026-05-16T01:16:15.982948"
}
```

**Errores:**
- 400: Parámetros faltantes

---

### GET /notifications

Lista todas las notificaciones registradas.

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "eventId": "evt-001",
    "orderId": "ord-123",
    "email": "test@example.com",
    "status": "SENT",
    "processedAt": "2026-05-16T01:16:15.982948"
  }
]
```

## Configuración RabbitMQ

| Property | Valor |
|----------|-------|
| Queue | `notification.order.created` |
| Exchange | `order.events` |
| Routing Key | `order.created` |
| Binding | queue → exchange con routing key `order.created` |

## Consumo de Eventos

El servicio escucha la cola `notification.order.created`. Al recibir un evento:

1. Deserializa el JSON a `OrderCreatedEvent`
2. Registra la notificación en PostgreSQL
3. Marca el estado como `SENT`

## Evento OrderCreatedEvent (consumido)

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "customerEmail": "cliente@test.com",
  "totalAmount": 150.0,
  "timestamp": "2026-05-16T01:26:12.168Z"
}
```

## Ejecutar Tests

```bash
mvn test
```