# 🛒 Mini Ecommerce Microservices Platform

A modern, scalable e-commerce platform built with microservices architecture using Spring Boot, Docker, and modern infrastructure components.

## 🏗️ **Architecture Overview**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Product       │    │   User          │    │   Order         │
│   Service       │    │   Service       │    │   Service       │
│   Port: 8086    │    │   Port: 8083    │    │   Port: 8084    │
│   DB: Products  │    │   DB: Users     │    │   DB: Orders    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐    ┌─────────────────┐
                    │   Payment       │    │   Inventory     │
                    │   Service       │    │   Service       │
                    │   Port: 8085    │    │   Port: 8082    │
                    │   DB: Payments  │    │   DB: MongoDB   │
                    └─────────────────┘    └─────────────────┘
```

## 🚀 **Features**

- **Microservices Architecture**: 5 independent services
- **Polyglot Persistence**: PostgreSQL + MongoDB
- **Event-Driven Communication**: Apache Kafka
- **Observability**: Prometheus, Grafana, Zipkin
- **Containerized**: Docker + Docker Compose
- **Modern Stack**: Spring Boot 3.5.4 + Java 21

## 🛠️ **Technology Stack**

### **Backend**

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: PostgreSQL 16, MongoDB 7
- **Messaging**: Apache Kafka 7.6.1

### **Infrastructure**

- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Caching**: Redis 7
- **Monitoring**: Prometheus, Grafana
- **Tracing**: Zipkin

## 📋 **Prerequisites**

- **Java**: JDK 21 or later
- **Docker**: Docker Desktop or Docker Engine
- **Docker Compose**: Version 3.8 or later
- **Git**: For version control

## 🚀 **Quick Start**

### **1. Clone the Repository**

```bash
git clone <repository-url>
cd mini-ecom
```

### **2. Start Infrastructure**

```bash
# Start all Docker containers
docker-compose -f docker/docker-compose.yml up -d

# Wait for containers to be ready
# Check status
docker ps
```

### **3. Start Services**

```bash
# Product Service
cd services/product-service
./gradlew bootRun -x test

# User Service (in new terminal)
cd services/user-service
./gradlew bootRun -x test

# Order Service (in new terminal)
cd services/order-service
./gradlew bootRun -x test

# Payment Service (in new terminal)
cd services/payment-service
./gradlew bootRun -x test

# Inventory Service (in new terminal)
cd services/inventory-service
./gradlew bootRun -x test
```

## 🌐 **Service Endpoints**

| Service   | Port | Health Check                          | Database              |
| --------- | ---- | ------------------------------------- | --------------------- |
| Product   | 8086 | http://localhost:8086/actuator/health | PostgreSQL (products) |
| User      | 8083 | http://localhost:8083/actuator/health | PostgreSQL (users)    |
| Order     | 8084 | http://localhost:8084/actuator/health | PostgreSQL (orders)   |
| Payment   | 8085 | http://localhost:8085/actuator/health | PostgreSQL (payments) |
| Inventory | 8082 | http://localhost:8082/actuator/health | MongoDB (inventory)   |

## 🗄️ **Database Connections**

### **PostgreSQL Services**

- **Users DB**: `localhost:5433`
- **Orders DB**: `localhost:5434`
- **Payments DB**: `localhost:5435`
- **Products DB**: `localhost:5436`

### **MongoDB**

- **Inventory DB**: `localhost:27017`

### **Other Services**

- **Kafka**: `localhost:9092`
- **Redis**: `localhost:6379`
- **Zipkin**: `localhost:9411`
- **Grafana**: `localhost:3000`

## 🔧 **Configuration**

### **Environment Variables**

Each service has its own `.env` file for configuration:

- `services/*/src/main/resources/application.yml` - Main configuration
- `.env` files - Environment-specific values (not committed to git)

### **Database Configuration**

- **Username**: `user`
- **Password**: `pass`
- **Driver**: `org.postgresql.Driver`

## 📊 **Current Status**

**Overall Progress**: **45% Complete**

- ✅ **Infrastructure**: 100% - All containers running
- ✅ **Configuration**: 95% - All services configured
- 🔴 **Service Startup**: 20% - Investigating startup issues
- ⏳ **Business Logic**: 0% - Pending
- ⏳ **API Development**: 0% - Pending

## 🚨 **Known Issues & Solutions**

See the [Issues Log](issues/ISSUES_LOG.md) for detailed information about:

- Problems encountered
- Solutions applied
- Current blockers
- Next steps

## 🆘 **Troubleshooting**

### **Quick Fixes**

- **Service won't start**: Check port availability and database connectivity
- **Database connection failed**: Verify Docker containers are running
- **Kafka issues**: Check Zookeeper and restart Kafka container

### **Detailed Guide**

See [Troubleshooting Guide](issues/TROUBLESHOOTING_GUIDE.md) for comprehensive solutions.

## 📁 **Project Structure**

```
mini-ecom/
├── docker/                          # Docker configuration
│   ├── docker-compose.yml          # Main compose file
│   ├── kafka/                      # Kafka configuration
│   ├── prometheus/                 # Prometheus config
│   └── grafana/                    # Grafana provisioning
├── services/                        # Microservices
│   ├── product-service/            # Product management
│   ├── user-service/               # User management
│   ├── order-service/              # Order processing
│   ├── payment-service/            # Payment handling
│   └── inventory-service/          # Inventory tracking
├── issues/                          # Documentation
│   ├── ISSUES_LOG.md               # Comprehensive issues log
│   ├── PROJECT_STATUS.md           # Current project status
│   └── TROUBLESHOOTING_GUIDE.md    # Quick fixes guide
├── ENVIRONMENT_SETUP.md            # Environment configuration guide
└── README.md                       # This file
```

## 🎯 **Development Roadmap**

### **Phase 1: Infrastructure & Services** 🟡 **IN PROGRESS**

- [x] Docker containers setup
- [x] Database connectivity
- [x] Kafka messaging
- [ ] Service startup verification
- [ ] Port binding verification

### **Phase 2: Basic Functionality** ⏳ **PENDING**

- [ ] JPA entities creation
- [ ] Basic CRUD operations
- [ ] REST endpoints
- [ ] Service-to-service communication

### **Phase 3: E-commerce Features** ⏳ **PENDING**

- [ ] Product catalog
- [ ] User management
- [ ] Order processing
- [ ] Payment integration
- [ ] Inventory management

## 🤝 **Contributing**

1. **Fork** the repository
2. **Create** a feature branch
3. **Make** your changes
4. **Test** thoroughly
5. **Submit** a pull request

## 📝 **License**

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 **Support**

- **Issues**: Check the [Issues Log](issues/ISSUES_LOG.md)
- **Troubleshooting**: See [Troubleshooting Guide](issues/TROUBLESHOOTING_GUIDE.md)
- **Status**: Check [Project Status](issues/PROJECT_STATUS.md)

---

**Last Updated**: August 31, 2025  
**Version**: 0.1.0  
**Status**: 🟡 Infrastructure Ready - Services Testing
