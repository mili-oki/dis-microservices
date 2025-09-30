# Microservices Architecture Diagram

## Architecture Overview

The system follows a microservices architecture pattern with:
- **API Gateway** for routing and security
- **Service Discovery** for service registration and discovery
- **Configuration Management** for centralized configuration
- **Message Queue** for asynchronous communication
- **Database per Service** pattern for data isolation

## Mermaid Diagram Code

```mermaid
graph TB
    subgraph "Client Layer"
        Client[Client Applications]
    end
    
    subgraph "API Gateway Layer"
        Gateway[Spring Cloud Gateway<br/>- JWT Authentication<br/>- Request Routing<br/>- Security Filtering]
    end
    
    subgraph "Infrastructure Services"
        Discovery[Eureka Discovery Service<br/>Port: 8761<br/>- Service Registry<br/>- Health Monitoring]
        Config[Config Server<br/>Port: 8888<br/>- Centralized Configuration<br/>- Native File System]
        RabbitMQ[RabbitMQ<br/>- Message Broker<br/>- Event Streaming]
    end
    
    subgraph "Business Services"
        Auth[Auth Service<br/>- User Registration<br/>- JWT Token Generation<br/>- Password Management]
        Catalog[Catalog Service<br/>- Product Management<br/>- Inventory Tracking<br/>- Product Search]
        Orders[Orders Service<br/>- Order Management<br/>- Status Transitions<br/>- Order Lifecycle]
        Payments[Payments Service<br/>- Payment Processing<br/>- Order Confirmation<br/>- Payment Validation]
        Notifications[Notifications Service<br/>- Payment Notifications<br/>- Event Logging<br/>- User Alerts]
    end
    
    subgraph "Data Layer"
        AuthDB[(Auth Database<br/>PostgreSQL)]
        CatalogDB[(Catalog Database<br/>PostgreSQL)]
        OrdersDB[(Orders Database<br/>PostgreSQL)]
        PaymentsDB[(Payments Database<br/>PostgreSQL)]
    end
    
    subgraph "Configuration Repository"
        ConfigRepo[Config Repository<br/>- application.yml<br/>- service-specific configs]
    end
    
    %% Client to Gateway
    Client --> Gateway
    
    %% Gateway to Services
    Gateway --> Auth
    Gateway --> Catalog
    Gateway --> Orders
    Gateway --> Payments
    Gateway --> Notifications
    
    %% Service Discovery
    Discovery -.-> Auth
    Discovery -.-> Catalog
    Discovery -.-> Orders
    Discovery -.-> Payments
    Discovery -.-> Notifications
    Discovery -.-> Gateway
    
    %% Configuration Management
    Config --> ConfigRepo
    Config -.-> Auth
    Config -.-> Catalog
    Config -.-> Orders
    Config -.-> Payments
    Config -.-> Notifications
    Config -.-> Gateway
    Config -.-> Discovery
    
    %% Database Connections
    Auth --> AuthDB
    Catalog --> CatalogDB
    Orders --> OrdersDB
    Payments --> PaymentsDB
    
    %% Inter-Service Communication
    Orders -->|Feign Client| Catalog
    Payments -->|Feign Client| Orders
    Payments -->|Feign Client| Notifications
    
    %% Message Queue Communication
    Payments -->|Publish Events| RabbitMQ
    RabbitMQ -->|Consume Events| Orders
    RabbitMQ -->|Consume Events| Notifications
    
    %% Styling
    classDef serviceClass fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef infraClass fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef dataClass fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef gatewayClass fill:#fff3e0,stroke:#e65100,stroke-width:2px
    
    class Auth,Catalog,Orders,Payments,Notifications serviceClass
    class Discovery,Config,RabbitMQ infraClass
    class AuthDB,CatalogDB,OrdersDB,PaymentsDB dataClass
    class Gateway gatewayClass
```

## Component Descriptions

### Infrastructure Services
- **Eureka Discovery Service**: Service registry and discovery
- **Config Server**: Centralized configuration management
- **RabbitMQ**: Message broker for asynchronous communication

### Business Services
- **Auth Service**: User authentication and JWT token management
- **Catalog Service**: Product catalog and inventory management
- **Orders Service**: Order lifecycle and status management
- **Payments Service**: Payment processing and validation
- **Notifications Service**: Event logging and user notifications

### Data Layer
- **Database per Service**: Each service has its own PostgreSQL database
- **Data Isolation**: Services don't share databases for better isolation

### Communication Patterns
- **Synchronous**: HTTP/REST APIs with Feign clients
- **Asynchronous**: Message queues for event-driven communication
- **Service Discovery**: Eureka for service registration and discovery
