# Microservices Sequence Diagram

## Order Processing Flow

The sequence diagram demonstrates:
- **User Authentication**: Registration and login flow
- **Product Browsing**: Catalog service interaction
- **Order Management**: Order creation and confirmation
- **Payment Processing**: Payment validation and processing
- **Notification Distribution**: Multi-channel notification delivery
- **Error Handling**: Rollback mechanisms for failed operations

## Mermaid Diagram Code

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant Auth as Auth Service
    participant Catalog as Catalog Service
    participant Orders as Orders Service
    participant Payments as Payments Service
    participant Notifications as Notifications Service
    participant RabbitMQ
    participant DB as Databases
    
    Note over Client,DB: Complete E-commerce Order Flow
    
    %% Authentication Flow
    Client->>Gateway: POST /auth/register
    Gateway->>Auth: Forward registration
    Auth->>DB: Save user with hashed password
    Auth-->>Gateway: User created
    Gateway-->>Client: 201 Created
    
    Client->>Gateway: POST /auth/login
    Gateway->>Auth: Forward login
    Auth->>DB: Validate credentials
    Auth-->>Gateway: JWT Token
    Gateway-->>Client: JWT Token
    
    %% Product Browsing
    Client->>Gateway: GET /products (with JWT)
    Gateway->>Gateway: Validate JWT
    Gateway->>Catalog: GET /products
    Catalog->>DB: Query products
    Catalog-->>Gateway: Product list
    Gateway-->>Client: Product list
    
    %% Order Creation
    Client->>Gateway: POST /orders?productId=1&quantity=2 (with JWT)
    Gateway->>Gateway: Validate JWT
    Gateway->>Orders: POST /orders
    Orders->>Catalog: GET /products/1 (Feign)
    Catalog-->>Orders: Product details
    Orders->>Orders: Validate stock availability
    Orders->>DB: Create order (PENDING)
    Orders-->>Gateway: Order created
    Gateway-->>Client: Order created
    
    %% Order Confirmation
    Client->>Gateway: PUT /orders/1/confirm (with JWT)
    Gateway->>Gateway: Validate JWT
    Gateway->>Orders: PUT /orders/1/confirm
    Orders->>DB: Update order status (CONFIRMED)
    Orders-->>Gateway: Order confirmed
    Gateway-->>Client: Order confirmed
    
    %% Payment Processing
    Client->>Gateway: POST /payments?orderId=1&amount=99.99 (with JWT)
    Gateway->>Gateway: Validate JWT
    Gateway->>Payments: POST /payments
    Payments->>Orders: GET /orders/1 (Feign)
    Orders-->>Payments: Order details
    Payments->>Payments: Validate order status
    Payments->>DB: Create payment record
    Payments->>Orders: PUT /orders/1/confirm (Feign)
    Orders->>DB: Update order status (CONFIRMED)
    
    %% Notification Distribution
    Payments->>Notifications: POST /notifications/payment (HTTP)
    Notifications-->>Payments: 202 Accepted
    
    Payments->>RabbitMQ: Publish to q.orders.payment-results
    Payments->>RabbitMQ: Publish to q.payment.notifications
    
    RabbitMQ->>Orders: Consume payment result
    Orders->>DB: Update order status (PAYED)
    
    RabbitMQ->>Notifications: Consume payment notification
    Notifications->>Notifications: Log notification
    
    Payments-->>Gateway: Payment processed
    Gateway-->>Client: Payment successful
    
    %% Error Handling (Alternative Flow)
    Note over Payments,Notifications: If notification fails
    Payments->>RabbitMQ: Publish FAILED event
    RabbitMQ->>Orders: Consume FAILED event
    Orders->>DB: Rollback order to PENDING
```

## Flow Description

### 1. Authentication Phase
- User registers with username and password
- Password is hashed using BCrypt
- User logs in and receives JWT token
- JWT token is used for subsequent authenticated requests

### 2. Product Browsing Phase
- Client requests product list with JWT token
- Gateway validates JWT token
- Catalog service returns available products
- Client can search and filter products

### 3. Order Creation Phase
- Client creates order with product ID and quantity
- Orders service validates product availability via Catalog service
- Order is created in PENDING status
- Stock validation ensures sufficient inventory

### 4. Order Confirmation Phase
- Client confirms the order
- Order status changes from PENDING to CONFIRMED
- Order becomes protected from cancellation

### 5. Payment Processing Phase
- Client initiates payment with order ID and amount
- Payments service validates order status
- Payment record is created with SUCCESS status
- Order is confirmed in Orders service

### 6. Notification Distribution Phase
- Notifications are sent via HTTP to Notifications service
- Events are published to RabbitMQ queues
- Orders service consumes payment results
- Notifications service consumes payment events

### 7. Error Handling Phase
- If notifications fail, FAILED events are published
- Orders service rolls back order status to PENDING
- System maintains consistency through compensation logic

## Key Integration Points

- **Synchronous Communication**: HTTP/REST APIs with Feign clients
- **Asynchronous Communication**: RabbitMQ message queues
- **Authentication**: JWT token validation at Gateway
- **Data Consistency**: Eventual consistency with compensation logic
- **Error Recovery**: Rollback mechanisms for failed operations
