# 🚨 Issues Log - Mini Ecommerce Project

## 📋 **Project Overview**

- **Project**: Mini Ecommerce Microservices Platform
- **Architecture**: 5 microservices (Product, User, Order, Payment, Inventory)
- **Infrastructure**: Docker containers with PostgreSQL, MongoDB, Kafka, Redis
- **Framework**: Spring Boot 3.5.4 with Java 21

---

## 🔍 **Issue #1: Database Connection Failures**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: 🔴 **RESOLVED**
- **Services Affected**: All PostgreSQL-based services

### **Symptoms**

- Services failing to start with database connection errors
- Error: `FATAL: password authentication failed for user "sa"`
- Error: `Could not resolve placeholder 'SERVER_PORT' in value "${SERVER_PORT}"`

### **Root Causes Identified**

1. **Configuration Typos**: `sever:` instead of `server:` in multiple services
2. **Wrong Application Names**: Services had incorrect names in config
3. **Database URL Format**: `jdbc:postgres://` instead of `jdbc:postgresql://`
4. **Missing Driver Class**: No `driver-class-name` specified
5. **Volume Mapping Issues**: Wrong volume names in docker-compose.yml
6. **Environment Variables**: Spring Boot not loading `.env` files

### **Solutions Applied**

1. ✅ **Fixed Configuration Typos**

   - `sever:` → `server:` in all services
   - Corrected application names for each service

2. ✅ **Fixed Database URLs**

   - `jdbc:postgres://` → `jdbc:postgresql://`
   - Added `driver-class-name: org.postgresql.Driver`

3. ✅ **Fixed Docker Volume Mapping**

   - Products service: `pgusers` → `pgproducts`

4. ✅ **Added Environment Support**
   - Added `spring-dotenv` dependency to all services
   - Created `.env` files for each service

### **Files Modified**

- `services/*/src/main/resources/application.yml` (all services)
- `services/*/build.gradle` (all services)
- `docker/docker-compose.yml`

---

## 🔍 **Issue #2: Kafka Container Startup Failures**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: ✅ **RESOLVED**
- **Services Affected**: Kafka messaging infrastructure

### **Symptoms**

- Kafka container exiting immediately after startup
- Error: `requirement failed: If you have two listeners on the same port then one needs to be IPv4 and the other IPv6`

### **Root Cause**

- **Listener Configuration Conflict**: Multiple listeners configured on same port with conflicting protocols

### **Solution Applied**

- ✅ **Simplified Kafka Configuration**
  - Removed duplicate listener configurations
  - Set single listener: `PLAINTEXT://0.0.0.0:9092`
  - Set single advertised listener: `PLAINTEXT://localhost:9092`

### **Files Modified**

- `docker/docker-compose.yml`

---

## 🔍 **Issue #3: PostgreSQL Container Permission Issues**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: ✅ **RESOLVED**
- **Services Affected**: PostgreSQL containers

### **Symptoms**

- PostgreSQL containers exiting with permission errors
- Error: `could not create file "pg_wal/xlogtemp.38": No such file or directory`
- Error: `fixing permissions on existing directory /var/lib/postgresql/data`

### **Root Cause**

- **Volume Permission Conflicts**: Existing volumes had permission issues
- **Container Restart Issues**: Containers couldn't initialize properly

### **Solution Applied**

- ✅ **Clean Volume Restart**
  - Stopped all containers: `docker-compose down`
  - Cleaned volumes: `docker volume prune -f`
  - Restarted containers: `docker-compose up -d`

### **Files Modified**

- None (Docker operations only)

---

## 🔍 **Issue #4: Timezone Configuration Errors**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: ✅ **RESOLVED**
- **Services Affected**: User Service

### **Symptoms**

- Error: `FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"`
- Service failing to connect to database

### **Root Cause**

- **Invalid Timezone Parameter**: PostgreSQL doesn't recognize `Asia/Calcutta`
- **URL Encoding Issues**: Complex timezone parameters in connection string

### **Solution Applied**

- ✅ **Simplified Database URL**
  - Removed timezone parameter: `?stringtype=unspecified&options=-c%20TimeZone=Asia/Kolkata`
  - Simplified to: `jdbc:postgresql://localhost:5433/users`

### **Files Modified**

- `services/user-service/src/main/resources/application.yml`

---

## 🔍 **Issue #5: Environment Variable Loading**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: 🔄 **IN PROGRESS**
- **Services Affected**: All services

### **Symptoms**

- Spring Boot not loading `.env` files automatically
- Services falling back to default values
- Environment-specific configuration not working

### **Root Cause**

- **Missing Dependency**: Spring Boot doesn't natively support `.env` files
- **Configuration Mismatch**: Environment variables not being resolved

### **Solutions Tried**

1. ✅ **Added spring-dotenv dependency**

   - Added `implementation("me.paulschwarz:spring-dotenv:4.0.0")` to all services

2. 🔄 **Temporarily reverted to direct values**
   - Using hardcoded values for testing
   - Will re-implement environment variables after services are running

### **Files Modified**

- `services/*/build.gradle` (all services)
- `services/*/src/main/resources/application.yml` (temporarily reverted)

---

## 🔍 **Issue #6: Service Startup Failures**

### **Problem Description**

- **Date**: August 31, 2025
- **Status**: 🔴 **INVESTIGATING**
- **Services Affected**: Product Service (primary test case)

### **Symptoms**

- Product service not starting on port 8086
- No clear error messages in logs
- Service appears to start but port not accessible

### **Current Investigation Status**

- ✅ **Dependencies**: All resolved and downloaded
- ✅ **Configuration**: Fixed and validated
- ✅ **Database**: Containers running and accessible
- 🔍 **Service Startup**: Investigating why service isn't binding to port

### **Next Steps**

1. Check service logs for startup errors
2. Verify database schema exists
3. Test with minimal configuration
4. Check for port conflicts

---

## 📊 **Current Status Summary**

### **✅ Resolved Issues**

- Database connection configuration
- Kafka startup configuration
- PostgreSQL container permissions
- Timezone configuration errors
- Basic service configuration

### **🔄 In Progress**

- Environment variable implementation
- Service startup verification

### **🔴 Open Issues**

- Product service not starting properly
- Need to verify all services can start

---

## 🎯 **Next Steps**

1. **Complete Service Testing**

   - Get Product Service running
   - Test all other services
   - Verify database connectivity

2. **Re-implement Environment Variables**

   - Once services are running
   - Test `.env` file loading
   - Implement proper configuration management

3. **Start Business Logic Development**
   - Create JPA entities
   - Implement REST controllers
   - Add Kafka event handling

---

## 📝 **Lessons Learned**

1. **Configuration Validation**: Always validate YAML syntax and property names
2. **Docker Volume Management**: Clean volumes when facing permission issues
3. **Service Dependencies**: Ensure all required dependencies are properly configured
4. **Incremental Testing**: Test one service at a time to isolate issues
5. **Documentation**: Keep detailed logs of issues and solutions

---

## 🔧 **Useful Commands**

```bash
# Check container status
docker ps

# View container logs
docker logs <container-name>

# Clean Docker volumes
docker volume prune -f

# Restart services
docker-compose -f docker/docker-compose.yml restart

# Test database connectivity
Test-NetConnection -ComputerName localhost -Port <port>

# Build and run service
./gradlew clean build -x test
./gradlew bootRun -x test
```

---

**Last Updated**: August 31, 2025  
**Status**: 🔴 **INVESTIGATING** - Product Service startup issues
