# 🚀 Quick Troubleshooting Guide

## 🚨 **Common Issues & Quick Fixes**

### **1. Service Won't Start**

```bash
# Check if port is already in use
Test-NetConnection -ComputerName localhost -Port <PORT>

# Check Java processes
Get-Process java -ErrorAction SilentlyContinue

# Check service logs
./gradlew bootRun --console=plain
```

### **2. Database Connection Failed**

```bash
# Check if containers are running
docker ps

# Check container logs
docker logs <container-name>

# Test database connectivity
Test-NetConnection -ComputerName localhost -Port <DB_PORT>
```

### **3. Kafka Not Working**

```bash
# Check Kafka container status
docker ps | findstr kafka

# Check Kafka logs
docker logs docker-kafka-1

# Restart Kafka
docker-compose -f docker/docker-compose.yml restart kafka
```

### **4. Docker Issues**

```bash
# Clean restart
docker-compose -f docker/docker-compose.yml down
docker volume prune -f
docker-compose -f docker/docker-compose.yml up -d
```

---

## 🔧 **Service-Specific Commands**

### **Product Service (Port 8086)**

```bash
cd services/product-service
./gradlew clean build -x test
./gradlew bootRun -x test
```

### **User Service (Port 8083)**

```bash
cd services/user-service
./gradlew clean build -x test
./gradlew bootRun -x test
```

### **Order Service (Port 8084)**

```bash
cd services/order-service
./gradlew clean build -x test
./gradlew bootRun -x test
```

### **Payment Service (Port 8085)**

```bash
cd services/payment-service
./gradlew clean build -x test
./gradlew bootRun -x test
```

### **Inventory Service (Port 8082)**

```bash
cd services/inventory-service
./gradlew clean build -x test
./gradlew bootRun -x test
```

---

## 📊 **Health Check Commands**

```bash
# Check all services
Test-NetConnection -ComputerName localhost -Port 8082  # Inventory
Test-NetConnection -ComputerName localhost -Port 8083  # User
Test-NetConnection -ComputerName localhost -Port 8084  # Order
Test-NetConnection -ComputerName localhost -Port 8085  # Payment
Test-NetConnection -ComputerName localhost -Port 8086  # Product

# Check infrastructure
Test-NetConnection -ComputerName localhost -Port 5433  # Users DB
Test-NetConnection -ComputerName localhost -Port 5434  # Orders DB
Test-NetConnection -ComputerName localhost -Port 5435  # Payments DB
Test-NetConnection -ComputerName localhost -Port 5436  # Products DB
Test-NetConnection -ComputerName localhost -Port 27017 # MongoDB
Test-NetConnection -ComputerName localhost -Port 9092  # Kafka
Test-NetConnection -ComputerName localhost -Port 6379  # Redis
```

---

## 🆘 **Emergency Reset**

If everything is broken:

```bash
# Stop everything
docker-compose -f docker/docker-compose.yml down

# Clean everything
docker system prune -f
docker volume prune -f

# Restart fresh
docker-compose -f docker/docker-compose.yml up -d

# Wait for containers to be ready
Start-Sleep 30
```
