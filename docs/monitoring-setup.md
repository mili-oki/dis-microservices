# Microservices Monitoring Setup Guide

## Overview

This guide explains how to set up comprehensive monitoring for the microservices architecture using Prometheus, Grafana, and Alertmanager with email notifications.

## Architecture

The monitoring stack includes:

- **Prometheus**: Metrics collection and storage
- **Grafana**: Visualization and dashboards
- **Alertmanager**: Alert routing and notifications
- **Micrometer**: Application metrics collection
- **Resilience4j**: Circuit breaker monitoring

## Services and Ports

| Service | Port | Purpose |
|---------|------|---------|
| Prometheus | 9090 | Metrics collection and querying |
| Grafana | 3000 | Dashboards and visualization |
| Alertmanager | 9093 | Alert management and notifications |

## Setup Instructions

### 1. Start the Monitoring Stack

```bash
# Start all services including monitoring
docker-compose up -d

# Or start only monitoring services
docker-compose up -d prometheus grafana alertmanager
```

### 2. Access the Services

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Alertmanager**: http://localhost:9093

### 3. Verify Metrics Collection

Check that Prometheus is collecting metrics from all services:

1. Go to http://localhost:9090/targets
2. Verify all services show as "UP"
3. Check the "Status" column for each target

## Monitoring Features

### 1. Application Metrics

Each microservice exposes the following metrics:

- **HTTP Request Metrics**: Request rate, response time, error rate
- **JVM Metrics**: Memory usage, GC statistics, thread count
- **System Metrics**: CPU usage, disk usage
- **Database Metrics**: Connection pool status, query performance

### 2. Circuit Breaker Monitoring

Resilience4j circuit breakers are configured for:

- **catalog-service**: Product validation calls
- **orders-service**: Order processing calls  
- **payments-service**: Payment processing calls

Circuit breaker states are monitored:
- **CLOSED**: Normal operation
- **OPEN**: Circuit is open, calls are failing
- **HALF_OPEN**: Testing if service has recovered

### 3. Custom Dashboards

The Grafana dashboard includes:

- **Request Rate**: Requests per second by service
- **Response Time**: 95th percentile response times
- **Error Rate**: Percentage of failed requests
- **Memory Usage**: JVM memory consumption
- **CPU Usage**: System CPU utilization
- **Circuit Breaker Status**: Real-time circuit breaker states

## Alerting Rules

### Critical Alerts

1. **Service Down**: When a service is unreachable
2. **Circuit Breaker Open**: When circuit breakers are open
3. **Database Connection Failure**: When database connections fail
4. **RabbitMQ Connection Failure**: When message broker is down

### Warning Alerts

1. **High Error Rate**: When error rate exceeds 10%
2. **High Response Time**: When 95th percentile > 2 seconds
3. **High Memory Usage**: When memory usage > 80%
4. **High CPU Usage**: When CPU usage > 80%
5. **High GC Pressure**: When garbage collection is frequent

## Email Notifications

### Configuration

Email notifications are configured in `monitoring/alertmanager.yml`:

```yaml
global:
  smtp_smarthost: 'localhost:587'
  smtp_from: 'alerts@microservices.local'
  smtp_auth_username: 'alerts@microservices.local'
  smtp_auth_password: 'password'
```

### Alert Routing

- **Critical Alerts**: Sent to admin@microservices.local
- **Warning Alerts**: Sent to admin@microservices.local
- **Circuit Breaker Alerts**: Sent to devops@microservices.local

### Email Templates

Alerts include:
- Alert summary and description
- Affected service name
- Severity level
- Timestamp
- Service-specific details

## Circuit Breaker Configuration

### Default Settings

```yaml
resilience4j:
  circuitbreaker:
    instances:
      catalog-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
        minimum-number-of-calls: 5
```

### Parameters Explained

- **failure-rate-threshold**: Percentage of failed calls to open circuit
- **wait-duration-in-open-state**: Time to wait before trying again
- **sliding-window-size**: Number of calls to evaluate
- **minimum-number-of-calls**: Minimum calls before evaluation

## Monitoring Endpoints

Each service exposes monitoring endpoints:

- `/actuator/health`: Health check
- `/actuator/prometheus`: Prometheus metrics
- `/actuator/metrics`: Application metrics
- `/actuator/circuitbreakers`: Circuit breaker status

## Troubleshooting

### Common Issues

1. **Metrics Not Appearing**: Check if services are registered with Eureka
2. **Alerts Not Firing**: Verify alert rules in Prometheus
3. **Email Not Sending**: Check SMTP configuration
4. **Dashboard Empty**: Ensure Prometheus is collecting metrics

### Debug Commands

```bash
# Check service health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/prometheus

# Check circuit breaker status
curl http://localhost:8080/actuator/circuitbreakers
```

## Performance Considerations

### Resource Usage

- **Prometheus**: ~100MB RAM, ~1GB disk per day
- **Grafana**: ~50MB RAM
- **Alertmanager**: ~20MB RAM

### Scaling

For production environments:

1. **Prometheus**: Use federation for multiple instances
2. **Grafana**: Use external database for persistence
3. **Alertmanager**: Use clustering for high availability

## Security

### Access Control

- **Grafana**: Change default admin password
- **Prometheus**: Use reverse proxy for authentication
- **Alertmanager**: Secure SMTP credentials

### Network Security

- Use internal networks for service communication
- Expose only necessary ports
- Implement TLS for external access

## Maintenance

### Regular Tasks

1. **Clean Old Data**: Configure retention policies
2. **Update Dashboards**: Add new metrics as needed
3. **Review Alerts**: Tune thresholds based on usage
4. **Backup Configurations**: Save dashboard and alert configurations

### Monitoring Best Practices

1. **Set Appropriate Thresholds**: Based on actual usage patterns
2. **Use Multiple Alert Levels**: Critical, warning, info
3. **Group Related Alerts**: Avoid alert fatigue
4. **Test Alerting**: Regularly verify alert delivery
5. **Document Runbooks**: Create response procedures

## Integration with CI/CD

### Automated Deployment

Include monitoring configuration in deployment pipelines:

1. **Health Checks**: Verify services are healthy
2. **Metrics Validation**: Ensure metrics are being collected
3. **Alert Testing**: Verify alerting is working
4. **Dashboard Updates**: Deploy dashboard changes

### Monitoring as Code

Store all monitoring configuration in version control:

- Prometheus configuration
- Grafana dashboards
- Alert rules
- Alertmanager configuration

This ensures consistency and enables automated deployment of monitoring changes.
