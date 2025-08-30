# 🌍 Environment Configuration Setup

This document explains how to set up and use environment variables for the mini-ecommerce microservices project.

## 📁 Environment Files Structure

```
mini-ecom/
├── env.config                           # Root-level shared configuration
├── services/
│   ├── product-service/
│   │   └── env.config                  # Product service specific config
│   ├── user-service/
│   │   └── env.config                  # User service specific config
│   ├── order-service/
│   │   └── env.config                  # Order service specific config
│   ├── payment-service/
│   │   └── env.config                  # Payment service specific config
│   └── inventory-service/
│       └── env.config                  # Inventory service specific config
```

## 🔧 How to Use Environment Variables

### **Option 1: Rename Files (Recommended)**

1. Rename `env.config` to `.env` in each directory
2. Spring Boot will automatically load `.env` files

### **Option 2: Set System Environment Variables**

Set these variables in your system before starting services:

```bash
# Windows PowerShell
$env:SERVER_PORT="8086"
$env:DB_HOST="localhost"
$env:DB_PASSWORD="your_secure_password"

# Windows Command Prompt
set SERVER_PORT=8086
set DB_HOST=localhost
set DB_PASSWORD=your_secure_password

# Linux/Mac
export SERVER_PORT=8086
export DB_HOST=localhost
export DB_PASSWORD=your_secure_password
```

### **Option 3: Command Line Arguments**

Start services with environment variables:

```bash
java -jar product-service.jar --SERVER_PORT=8086 --DB_PASSWORD=your_password
```

## 🔐 Security Best Practices

### **Production Environment:**

1. **NEVER commit `.env` files to version control**
2. Use secure secret management (AWS Secrets Manager, HashiCorp Vault, etc.)
3. Rotate passwords regularly
4. Use strong, unique passwords for each service

### **Development Environment:**

1. Use `.env.local` for local overrides
2. Keep sensitive data out of shared configs
3. Use different credentials for dev/staging/prod

## 📋 Environment Variables Reference

### **Common Variables (Root env.config):**

```bash
# Infrastructure
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
ZOOKEEPER_HOST=localhost
REDIS_HOST=localhost

# Observability
ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans
PROMETHEUS_ENDPOINT=http://localhost:9090

# Database (Common)
DB_USERNAME=user
DB_PASSWORD=pass
```

### **Service-Specific Variables:**

```bash
# Product Service
SERVER_PORT=8086
DB_HOST=localhost
DB_PORT=5436
DB_NAME=products

# User Service
SERVER_PORT=8083
DB_HOST=localhost
DB_PORT=5433
DB_NAME=users

# Order Service
SERVER_PORT=8084
DB_HOST=localhost
DB_PORT=5434
DB_NAME=orders

# Payment Service
SERVER_PORT=8085
DB_HOST=localhost
DB_PORT=5435
DB_NAME=payments

# Inventory Service
SERVER_PORT=8082
MONGODB_HOST=localhost
MONGODB_PORT=27017
MONGODB_DATABASE=inventory
```

## 🚀 Starting Services with Environment Variables

### **Using .env files:**

1. Rename `env.config` to `.env` in each service directory
2. Start services normally - Spring Boot will auto-load `.env` files

### **Using system environment variables:**

```bash
# Set variables
$env:DB_PASSWORD="your_secure_password"
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"

# Start service
./gradlew bootRun
```

## 🔍 Troubleshooting

### **Common Issues:**

1. **"Could not resolve placeholder"** - Environment variable not set
2. **"Connection refused"** - Check if Docker containers are running
3. **"Authentication failed"** - Verify database credentials

### **Debug Environment Variables:**

Add this to `application.yml` to see what values are being used:

```yaml
logging:
  level:
    org.springframework.boot.env: DEBUG
```

## 📝 Example .env File

```bash
# Example .env file for product-service
SERVER_PORT=8086
DB_HOST=localhost
DB_PORT=5436
DB_NAME=products
DB_USERNAME=user
DB_PASSWORD=your_secure_password
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=product-service
ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans
TRACING_SAMPLING_PROBABILITY=1.0
```

## 🎯 Next Steps

1. **Rename** `env.config` files to `.env`
2. **Update passwords** to secure values
3. **Test** each service with new configuration
4. **Commit** only the template files (not actual `.env` files)
5. **Document** any additional environment-specific configurations

---

**⚠️ Important:** Never commit actual `.env` files with real passwords to version control!
