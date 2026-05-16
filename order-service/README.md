# order-service

Microservicio de gestión de pedidos. Publica eventos `order.created` a RabbitMQ al crear un pedido.

## Endpoints

### POST /orders

Crea un nuevo pedido y emite evento a RabbitMQ.

**Body:**
```json
{
  "customerId": "cust-001",
  "email": "cliente@test.com",
  "total": 150.00,
  "currency": "USD"
}
```

**Respuesta (201):**
```json
{
  "id": 1,
  "customerId": "cust-001",
  "email": "cliente@test.com",
  "total": 150.00,
  "currency": "USD",
  "createdAt": "2026-05-16T01:26:12.168010914"
}
```

**Errores:**
- 400: Validación fallida (campos requeridos)
- 500: Error interno

---

### GET /orders/{id}

Obtiene un pedido por ID.

**Respuesta (200):**
```json
{
  "id": 1,
  "customerId": "cust-001",
  "email": "cliente@test.com",
  "total": 150.00,
  "currency": "USD",
  "createdAt": "2026-05-16T01:26:12.168010914"
}
```

**Errores:**
- 404: Pedido no encontrado

## Configuración RabbitMQ

| Property | Valor |
|----------|-------|
| Exchange | `order.events` |
| Tipo | topic |
| Routing Key | `order.created` |

## Evento order.created

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