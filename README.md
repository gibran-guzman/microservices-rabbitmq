# microservices-rabbitmq

Sistema de microservicios para procesamiento de pedidos con comunicación asíncrona basada en RabbitMQ.

## Descripción

Arquitectura de microservicios donde `order-service` Procesa pedidos y emite eventos a través de RabbitMQ, mientras `notification-service` consume dichos eventos para registrar notificaciones. Utiliza PostgreSQL para persistencia y RabbitMQ como message broker.

## Requisitos Previos

| Herramienta | Versión Mínima |
|-------------|----------------|
| Java | 21 |
| Maven | 3.9+ |
| Docker | 24.0+ |
| Docker Compose | 2.20+ |

## Instalación

```bash
# 1. Clonar el repositorio
git clone <repo-url>
cd microservices-rabbitmq

# 2. Iniciar infraestructura (PostgreSQL, RabbitMQ)
docker-compose up -d

# 3. Compilar order-service
cd order-service
mvn clean package -DskipTests

# 4. Compilar notification-service
cd ../notification-service
mvn clean package -DskipTests
```

## Ejemplo de Uso Rápido

```bash
# Iniciar order-service (puerto 8081)
java -jar order-service/target/order-service-0.0.1-SNAPSHOT.jar --server.port=8081 &

# Iniciar notification-service (puerto 8082)
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar &

# Crear un pedido (dispara evento order.created)
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "cust-001", "email": "cliente@test.com", "total": 150.00, "currency": "USD"}'

# Verificar notificaciones en notification-service
curl http://localhost:8082/notifications
```

## Estructura del Proyecto

```
microservices-rabbitmq/
├── docker-compose.yml          # PostgreSQL (5433, 5434), RabbitMQ (5672, 15672)
├── order-service/              # Servicio de pedidos
│   ├── src/main/java/.../
│   │   ├── config/             # RabbitMQConfig (exchange: order.events)
│   │   ├── controller/         # OrderController (POST/GET /orders)
│   │   ├── dto/                # CreateOrderRequest, OrderResponse
│   │   ├── entity/             # Order JPA entity
│   │   ├── event/              # OrderCreatedEvent DTO
│   │   └── repository/         # OrderRepository
│   └── pom.xml
├── notification-service/       # Servicio de notificaciones
│   ├── src/main/java/.../
│   │   ├── config/             # RabbitMQConfig (queue: notification.order.created)
│   │   ├── consumer/            # OrderEventConsumer (@RabbitListener)
│   │   ├── controller/         # NotificationController (POST/GET /notifications)
│   │   ├── dto/                # NotificationResponse
│   │   ├── entity/             # NotificationLog JPA entity
│   │   ├── event/              # OrderCreatedEvent (recibido)
│   │   ├── repository/         # NotificationRepository
│   │   └── service/           # NotificationService
│   └── pom.xml
└── README.md
```

## Variables de Entorno

### order-service

| Variable | Descripción | Ejemplo | Obligatoria |
|----------|-------------|---------|-------------| 
| `SERVER_PORT` | Puerto del servicio | `8081` | No |
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5434/orderdb` | Sí |
| `SPRING_DATASOURCE_USERNAME` | Usuario DB | `postgres` | Sí |
| `SPRING_DATASOURCE_PASSWORD` | Password DB | `postgres` | Sí |
| `SPRING_RABBITMQ_HOST` | Host RabbitMQ | `localhost` | Sí |
| `SPRING_RABBITMQ_PORT` | Puerto RabbitMQ | `5672` | Sí |

### notification-service

| Variable | Descripción | Ejemplo | Obligatoria |
|----------|-------------|---------|-------------|
| `SERVER_PORT` | Puerto del servicio | `8082` | No |
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5433/notificationdb` | Sí |
| `SPRING_DATASOURCE_USERNAME` | Usuario DB | `postgres` | Sí |
| `SPRING_DATASOURCE_PASSWORD` | Password DB | `postgres` | Sí |
| `SPRING_RABBITMQ_HOST` | Host RabbitMQ | `localhost` | Sí |
| `SPRING_RABBITMQ_PORT` | Puerto RabbitMQ | `5672` | Sí |

## Ejecutar Tests

```bash
# Tests de order-service
cd order-service
mvn test

# Tests de notification-service
cd ../notification-service
mvn test
```

## Arquitectura de Mensajería

```
┌─────────────┐    order.created     ┌──────────────────┐    notification.order.created    ┌────────────────────┐
│ order-      │ ──────────────────► │  order.events   │ ───────────────────────────────► │ notification-      │
│ service     │   (exchange topic)  │   (exchange)    │        (queue binding)           │ service            │
└─────────────┘                      └──────────────────┘                                   └────────────────────┘
```

| Componente | Nombre | Tipo |
|------------|--------|------|
| Exchange | `order.events` | topic |
| Queue | `notification.order.created` | durable |
| Routing Key | `order.created` | binding |

## Contribuir

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit con mensajes convencionales
4. Push y crear Pull Request