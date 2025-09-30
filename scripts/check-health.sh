#!/bin/bash

# Health check script for microservices
echo "Microservices Health Check"
echo "========================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check service health
check_service() {
    local service=$1
    local port=$2
    local url="http://localhost:$port/actuator/health"
    
    echo -n "Checking $service... "
    
    response=$(curl -s "$url" 2>/dev/null)
    
    if echo "$response" | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✓ HEALTHY${NC}"
        return 0
    else
        echo -e "${RED}✗ UNHEALTHY${NC}"
        return 1
    fi
}

# Function to check if service is running
check_running() {
    local service=$1
    local port=$2
    
    echo -n "Checking if $service is running... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port" 2>/dev/null)
    
    if [ "$response" = "200" ] || [ "$response" = "404" ] || [ "$response" = "401" ]; then
        echo -e "${GREEN}✓ RUNNING${NC}"
        return 0
    else
        echo -e "${RED}✗ NOT RUNNING${NC}"
        return 1
    fi
}

echo ""
echo "1. Core Services"
echo "----------------"

check_running "Gateway" "8080"
check_running "Discovery Service" "8761"
check_running "Config Server" "8888"

echo ""
echo "2. Business Services"
echo "--------------------"

check_running "Auth Service" "8086"
check_running "Catalog Service" "8082"
check_running "Orders Service" "8083"
check_running "Payments Service" "8084"
check_running "Notifications Service" "8085"

echo ""
echo "3. Health Endpoints"
echo "-------------------"

check_service "Gateway" "8080"
check_service "Auth Service" "8086"
check_service "Catalog Service" "8082"
check_service "Orders Service" "8083"
check_service "Payments Service" "8084"
check_service "Notifications Service" "8085"

echo ""
echo "4. Monitoring Services"
echo "-----------------------"

check_running "Prometheus" "9090"
check_running "Grafana" "3000"
check_running "Alertmanager" "9093"

echo ""
echo "5. Infrastructure Services"
echo "---------------------------"

check_running "RabbitMQ Management" "15672"
check_running "MailHog" "8025"

echo ""
echo "Health check completed!"
echo ""
echo "Service URLs:"
echo "- Gateway: http://localhost:8080"
echo "- Prometheus: http://localhost:9090"
echo "- Grafana: http://localhost:3000"
echo "- RabbitMQ: http://localhost:15672"
echo "- MailHog: http://localhost:8025"
