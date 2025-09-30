#!/bin/bash

# Test script for monitoring setup
echo "Testing Microservices Monitoring Setup"
echo "====================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local url=$1
    local name=$2
    local expected_status=$3
    
    echo -n "Testing $name... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
    if [ "$response" = "$expected_status" ] || [ "$response" = "302" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $response, expected $expected_status)"
        return 1
    fi
}

# Function to test service health
test_service_health() {
    local service=$1
    local port=$2
    
    echo -n "Testing $service health... "
    
    response=$(curl -s "http://localhost:$port/actuator/health" 2>/dev/null)
    
    if echo "$response" | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✓ UP${NC}"
        return 0
    else
        echo -e "${RED}✗ DOWN${NC}"
        return 1
    fi
}

# Function to test metrics endpoint
test_metrics() {
    local service=$1
    local port=$2
    
    echo -n "Testing $service metrics... "
    
    response=$(curl -s "http://localhost:$port/actuator/prometheus" 2>/dev/null)
    
    if echo "$response" | grep -q "http_server_requests_seconds_count"; then
        echo -e "${GREEN}✓ METRICS AVAILABLE${NC}"
        return 0
    else
        echo -e "${RED}✗ NO METRICS${NC}"
        return 1
    fi
}

echo ""
echo "1. Testing Monitoring Services"
echo "-------------------------------"

# Test Prometheus
test_endpoint "http://localhost:9090" "Prometheus" "200"

# Test Grafana
test_endpoint "http://localhost:3000" "Grafana" "200"

# Test Alertmanager
test_endpoint "http://localhost:9093" "Alertmanager" "200"

# Test MailHog
test_endpoint "http://localhost:8025" "MailHog" "200"

echo ""
echo "2. Testing Microservices Health"
echo "--------------------------------"

# Test all microservices
test_service_health "Gateway" "8080"
test_service_health "Auth Service" "8086"
test_service_health "Catalog Service" "8082"
test_service_health "Orders Service" "8083"
test_service_health "Payments Service" "8084"
test_service_health "Notifications Service" "8085"

echo ""
echo "3. Testing Metrics Collection"
echo "------------------------------"

# Test metrics endpoints
test_metrics "Gateway" "8080"
test_metrics "Auth Service" "8086"
test_metrics "Catalog Service" "8082"
test_metrics "Orders Service" "8083"
test_metrics "Payments Service" "8084"
test_metrics "Notifications Service" "8085"

echo ""
echo "4. Testing Prometheus Targets"
echo "------------------------------"

# Test if Prometheus can scrape all targets
echo -n "Checking Prometheus targets... "
targets_response=$(curl -s "http://localhost:9090/api/v1/targets" 2>/dev/null)

if echo "$targets_response" | grep -q '"health":"up"'; then
    echo -e "${GREEN}✓ TARGETS UP${NC}"
else
    echo -e "${YELLOW}⚠ SOME TARGETS DOWN${NC}"
fi

echo ""
echo "5. Testing Circuit Breaker Endpoints"
echo "------------------------------------"

# Test circuit breaker endpoints
test_endpoint "http://localhost:8083/actuator/circuitbreakers" "Orders Service Circuit Breakers" "200"

echo ""
echo "6. Monitoring URLs"
echo "------------------"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin)"
echo "Alertmanager: http://localhost:9093"
echo "MailHog: http://localhost:8025"
echo ""

echo "7. Quick Health Check Commands"
echo "-------------------------------"
echo "curl http://localhost:8080/actuator/health"
echo "curl http://localhost:8080/actuator/prometheus | grep http_server_requests"
echo "curl http://localhost:8083/actuator/circuitbreakers"
echo ""

echo "Monitoring setup test completed!"
