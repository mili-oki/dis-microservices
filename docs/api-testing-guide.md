# API Testing Guide - cURL Examples

This guide provides comprehensive cURL examples for testing all microservices APIs through the Spring Cloud Gateway.

## Prerequisites

- All services running locally
- Gateway running on port 8080
- Services running on their respective ports:
  - Auth Service: 8086
  - Catalog Service: 8082
  - Orders Service: 8083
  - Payments Service: 8084
  - Notifications Service: 8085

## Gateway Configuration

The gateway routes requests to services using the following pattern:
- `/auth-service/**` → Auth Service
- `/catalog-service/**` → Catalog Service
- `/orders-service/**` → Orders Service
- `/payments-service/**` → Payments Service
- `/notifications-service/**` → Notifications Service

## 1. Authentication Service Testing

### User Registration
```bash
# Register a new user
curl -X POST http://localhost:8080/auth-service/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### User Login
```bash
# Login and get JWT token
curl -X POST http://localhost:8080/auth-service/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Test with Invalid Credentials
```bash
# Test with wrong password
curl -X POST http://localhost:8080/auth-service/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "wrongpassword"
  }'
```

## 2. Catalog Service Testing

### Get All Products
```bash
# Get all products
curl -X GET http://localhost:8080/catalog-service/products
```

### Search Products
```bash
# Search products by name
curl -X GET "http://localhost:8080/catalog-service/products?q=laptop"
```

### Get Product by ID
```bash
# Get specific product
curl -X GET http://localhost:8080/catalog-service/products/1
```

### Create New Product
```bash
# Create a new product
curl -X POST http://localhost:8080/catalog-service/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Mouse",
    "price": 29.99,
    "stock": 50
  }'
```

### Update Product
```bash
# Update existing product
curl -X PUT http://localhost:8080/catalog-service/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "price": 1099.99,
    "stock": 15
  }'
```

### Delete Product
```bash
# Delete product
curl -X DELETE http://localhost:8080/catalog-service/products/1
```

## 3. Orders Service Testing

### Create Order
```bash
# Create a new order
curl -X POST "http://localhost:8080/orders-service/orders?productId=1&quantity=2"
```

### Get All Orders
```bash
# Get all orders
curl -X GET http://localhost:8080/orders-service/orders
```

### Get Order by ID
```bash
# Get specific order
curl -X GET http://localhost:8080/orders-service/orders/1
```

### Update Order Quantity
```bash
# Update order quantity
curl -X PUT "http://localhost:8080/orders-service/orders/1/quantity?quantity=3"
```

### Confirm Order
```bash
# Confirm order
curl -X PUT http://localhost:8080/orders-service/orders/1/confirm
```

### Cancel Order
```bash
# Cancel order
curl -X PUT http://localhost:8080/orders-service/orders/1/cancel
```

### Delete Order
```bash
# Delete order
curl -X DELETE http://localhost:8080/orders-service/orders/1
```

## 4. Payments Service Testing

### Process Payment
```bash
# Process payment for an order
curl -X POST "http://localhost:8080/payments-service/payments?orderId=1&amount=99.99"
```

### Get All Payments
```bash
# Get all payments
curl -X GET http://localhost:8080/payments-service/payments
```

### Get Payment by ID
```bash
# Get specific payment
curl -X GET http://localhost:8080/payments-service/payments/1
```

## 5. Notifications Service Testing

### Send Payment Notification
```bash
# Send payment notification
curl -X POST http://localhost:8080/notifications-service/notifications/payment \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 99.99,
    "status": "SUCCESS",
    "message": "Payment processed successfully"
  }'
```

## 6. Complete E-commerce Flow Testing

### Step 1: Register User
```bash
curl -X POST http://localhost:8080/auth-service/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "password": "password123"
  }'
```

### Step 2: Login and Get Token
```bash
# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/auth-service/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "password": "password123"
  }' | jq -r '.token')

echo "JWT Token: $TOKEN"
```

### Step 3: Browse Products
```bash
# Get all products
curl -X GET http://localhost:8080/catalog-service/products
```

### Step 4: Create Order
```bash
# Create order for product ID 1 with quantity 2
curl -X POST "http://localhost:8080/orders-service/orders?productId=1&quantity=2"
```

### Step 5: Confirm Order
```bash
# Confirm the order
curl -X PUT http://localhost:8080/orders-service/orders/1/confirm
```

### Step 6: Process Payment
```bash
# Process payment
curl -X POST "http://localhost:8080/payments-service/payments?orderId=1&amount=199.98"
```

## 7. Error Testing

### Test Invalid Product ID
```bash
# Try to create order with non-existent product
curl -X POST "http://localhost:8080/orders-service/orders?productId=999&quantity=1"
```

### Test Insufficient Stock
```bash
# Try to order more than available stock
curl -X POST "http://localhost:8080/orders-service/orders?productId=1&quantity=1000"
```

### Test Invalid Order Status
```bash
# Try to cancel a confirmed order
curl -X PUT http://localhost:8080/orders-service/orders/1/cancel
```

## 8. Health Check Endpoints

### Gateway Health
```bash
# Check gateway health
curl -X GET http://localhost:8080/actuator/health
```

### Service Health Checks
```bash
# Check auth service health
curl -X GET http://localhost:8080/auth-service/actuator/health

# Check catalog service health
curl -X GET http://localhost:8080/catalog-service/actuator/health

# Check orders service health
curl -X GET http://localhost:8080/orders-service/actuator/health

# Check payments service health
curl -X GET http://localhost:8080/payments-service/actuator/health

# Check notifications service health
curl -X GET http://localhost:8080/notifications-service/actuator/health
```

## 9. Gateway Routes Information

### View Gateway Routes
```bash
# Get gateway routes
curl -X GET http://localhost:8080/actuator/gateway/routes
```

### View Gateway Filters
```bash
# Get gateway filters
curl -X GET http://localhost:8080/actuator/gateway/filters
```

## 10. Testing with Authentication (When Security is Enabled)

### Using JWT Token in Headers
```bash
# Example with JWT token (when security is enabled)
curl -X GET http://localhost:8080/catalog-service/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Testing Protected Endpoints
```bash
# Test protected endpoint without token (should return 401)
curl -X GET http://localhost:8080/orders-service/orders

# Test protected endpoint with token
curl -X GET http://localhost:8080/orders-service/orders \
  -H "Authorization: Bearer $TOKEN"
```

## Service Discovery & Gateway Logs

### Check Service Discovery
```bash
# Check Eureka registry
curl -X GET http://localhost:8761/eureka/apps
```

### Check Gateway Logs
```bash
# View gateway routes and filters
curl -X GET http://localhost:8080/actuator/gateway/routes
curl -X GET http://localhost:8080/actuator/gateway/filters
```

### Test Service Connectivity
```bash
# Test direct service access 
curl -X GET http://localhost:8082/products
curl -X GET http://localhost:8083/orders
curl -X GET http://localhost:8084/payments
```

## Notes

1. **Development Mode**: Security is disabled in development mode for easier testing
2. **Service Ports**: Make sure all services are running on their configured ports
3. **Database**: Ensure PostgreSQL is running and databases are created
4. **Message Queue**: Ensure RabbitMQ is running for asynchronous communication
5. **Service Discovery**: Ensure Eureka server is running for service registration
