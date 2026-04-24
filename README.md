# 🛒 Mini Ecommerce Microservices Platform

A fully functional e-commerce backend built with Spring Boot microservices, Kafka event-driven communication, and polyglot persistence.

---

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  product-service│    │  user-service   │    │  order-service  │
│  Port: 8086     │    │  Port: 8083     │    │  Port: 8084     │
│  DB: PostgreSQL │    │  DB: PostgreSQL │    │  DB: PostgreSQL │
└─────────────────┘    └─────────────────┘    └────────┬────────┘
                                                        │ Kafka
                                              ┌─────────▼────────┐
                                              │  order-placed    │
                                              │  (Kafka topic)   │
                                              └──────┬─────┬─────┘
                                                     │     │
                                          ┌──────────▼─┐ ┌─▼──────────────┐
                                          │  inventory │ │payment-service │
                                          │  service   │ │Port: 8085      │
                                          │  Port:8082 │ │DB: PostgreSQL  │
                                          │  DB: Mongo │ └────────────────┘
                                          └────────────┘
```

**Event flow:** `POST /api/orders` → publishes `order-placed` to Kafka → inventory-service deducts stock + payment-service auto-processes payment.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5.4 |
| Language | Java 21 |
| Build | Gradle |
| Databases | PostgreSQL 16 (4x), MongoDB 7 |
| Messaging | Apache Kafka |
| Caching | Redis 7 |
| Tracing | Zipkin |
| Monitoring | Prometheus + Grafana |
| Containers | Docker + Docker Compose |

---

## Prerequisites

- Java 21+
- Docker Desktop
- Git

---

## Running the App

### Step 1 — Start infrastructure

```bash
docker-compose -f docker/docker-compose.yml up -d
```

This starts: PostgreSQL (x4), MongoDB, Kafka, Zookeeper, Redis, Zipkin, Prometheus, Grafana.

### Step 2 — Start each service in a separate terminal

```bash
cd services/product-service   && ./gradlew bootRun
cd services/user-service      && ./gradlew bootRun
cd services/inventory-service && ./gradlew bootRun
cd services/order-service     && ./gradlew bootRun
cd services/payment-service   && ./gradlew bootRun
```

> When you see `80% EXECUTING` — the service is running. That's normal for Gradle's bootRun.

### Step 3 — Verify all services are up

```bash
curl http://localhost:8086/actuator/health  # product
curl http://localhost:8083/actuator/health  # user
curl http://localhost:8082/actuator/health  # inventory
curl http://localhost:8084/actuator/health  # order
curl http://localhost:8085/actuator/health  # payment
```

All should return `{"status":"UP"}`.

---

## Service Ports

| Service | Port |
|---|---|
| product-service | 8086 |
| user-service | 8083 |
| inventory-service | 8082 |
| order-service | 8084 |
| payment-service | 8085 |
| Grafana | 3000 |
| Zipkin | 9411 |
| Prometheus | 9090 |

---

## API Reference

### Product Service — `localhost:8086`

#### Create a product
```http
POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 999.99,
  "stock": 50
}
```

#### Get all products
```http
GET /api/products
```

#### Get product by ID
```http
GET /api/products/{id}
```

#### Update a product
```http
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "Laptop Pro",
  "description": "Updated Gaming Laptop",
  "price": 1199.99,
  "stock": 100
}
```

#### Delete a product
```http
DELETE /api/products/{id}
```

---

### User Service — `localhost:8083`

#### Create a user
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9999999999"
}
```

> Email must be unique. Duplicate emails are rejected.

#### Get all users
```http
GET /api/users
```

#### Get user by ID
```http
GET /api/users/{id}
```

#### Update a user
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.new@example.com",
  "phone": "8888888888"
}
```

#### Delete a user
```http
DELETE /api/users/{id}
```

---

### Inventory Service — `localhost:8082`

#### Set / update inventory for a product
```http
POST /api/inventory
Content-Type: application/json

{
  "productId": 1,
  "quantity": 50
}
```

> This is an upsert — creates if not exists, updates if it does.

#### Get inventory by product ID
```http
GET /api/inventory/product/{productId}
```

#### Get all inventory records
```http
GET /api/inventory
```

---

### Order Service — `localhost:8084`

#### Place an order
```http
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "productId": 1,
  "quantity": 3,
  "totalPrice": 2999.97
}
```

> Placing an order automatically publishes an `order-placed` Kafka event.
> inventory-service will deduct stock and payment-service will create a payment — both happen automatically.

#### Get all orders
```http
GET /api/orders
```

#### Get order by ID
```http
GET /api/orders/{id}
```

#### Get orders by user
```http
GET /api/orders/user/{userId}
```

---

### Payment Service — `localhost:8085`

> Payments are created automatically when an order is placed. No manual payment creation needed.

#### Get all payments
```http
GET /api/payments
```

#### Get payment by order ID
```http
GET /api/payments/order/{orderId}
```

#### Get payments by user ID
```http
GET /api/payments/user/{userId}
```

---

## Typical Usage Flow

Follow this sequence to test the full end-to-end flow:

**1. Create a product**
```bash
curl -X POST http://localhost:8086/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Gaming Laptop","price":999.99,"stock":50}'
```

**2. Create a user**
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"9999999999"}'
```

**3. Set inventory for the product**
```bash
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":50}'
```

**4. Place an order**
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":3,"totalPrice":2999.97}'
```

**5. Verify inventory was deducted**
```bash
curl http://localhost:8082/api/inventory/product/1
# quantity should be 47 (50 - 3)
```

**6. Verify payment was auto-created**
```bash
curl http://localhost:8085/api/payments/order/1
# status should be SUCCESS
```

---

## Observability

| Tool | URL | Credentials |
|---|---|---|
| Grafana | http://localhost:3000 | admin / admin |
| Zipkin (tracing) | http://localhost:9411 | — |
| Prometheus | http://localhost:9090 | — |

Each service exposes metrics at `/actuator/prometheus` and traces are sent to Zipkin automatically.

---

## Redis Caching

Redis caching is enabled on **product-service** and **user-service** with a 5 minute TTL.

| Cache Key | Triggered By | Invalidated On |
|---|---|---|
| `product::{id}` | `GET /api/products/{id}` | update, delete |
| `products` | `GET /api/products` | create, update, delete |
| `user::{id}` | `GET /api/users/{id}` | update, delete |
| `users` | `GET /api/users` | create, update, delete |

You can verify caching is working by making the same GET request twice and checking that the second response is significantly faster (no DB query in logs).

Redis runs on `localhost:6379` via Docker. No extra setup needed.

---

## Observability

### Prometheus — `http://localhost:9090`

Prometheus scrapes all 5 services every 15 seconds from `/actuator/prometheus`.

To verify targets are up: go to `http://localhost:9090/targets` — all 5 should show state `UP`.

Useful queries to run in the Prometheus UI:

```promql
# HTTP requests per second per service
sum(rate(http_server_requests_seconds_count[1m])) by (job)

# P99 latency in ms
histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[1m])) by (le, job)) * 1000

# JVM heap used in MB
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# DB connection pool active
hikaricp_connections_active

# Service uptime
process_uptime_seconds
```

### Grafana — `http://localhost:3000`

Login with `admin` / `admin` (click Skip when asked to change password).

Navigate to: Dashboards → Browse → Mini Ecom → **Mini Ecom - Services Overview**

The dashboard has 8 pre-configured panels:

| Panel | What it shows |
|---|---|
| HTTP Requests/sec | Request rate per service |
| HTTP Error Rate (5xx) | Error rate per service |
| P99 Latency | 99th percentile response time in ms |
| JVM Heap Used | Heap memory consumption per service |
| JVM Threads | Live thread count per service |
| Service Uptime | Color-coded uptime (red < 30s, yellow < 60s, green) |
| DB Connection Pool | Active HikariCP connections |
| Kafka Consumer Lag | Consumer lag for inventory and payment services |

> If panels show "No data", make sure all 5 services are running and Prometheus targets are UP. Panels populate within one scrape interval (15 seconds).

### Zipkin — `http://localhost:9411`

Distributed tracing is enabled on all services. Every HTTP request generates a trace that you can search by service name in the Zipkin UI.

---

## Stopping the App

Stop all services with `Ctrl+C` in each terminal, then stop Docker:

```bash
docker-compose -f docker/docker-compose.yml down
```

To also remove all data volumes:

```bash
docker-compose -f docker/docker-compose.yml down -v
```
