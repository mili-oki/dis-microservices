#!/bin/bash

# Osnovni test monitoring setup-a
echo "Testing Basic Monitoring Setup"
echo "=============================="

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
echo "1. Testing Basic Monitoring Services"
echo "------------------------------------"

# Test Prometheus
test_endpoint "http://localhost:9090" "Prometheus" "200"

# Test Grafana
test_endpoint "http://localhost:3000" "Grafana" "200"

# Test Alertmanager
test_endpoint "http://localhost:9093" "Alertmanager" "200"

# Test MailHog
test_endpoint "http://localhost:8025" "MailHog" "200"

echo ""
echo "2. Testing Basic Health"
echo "----------------------"

# Test basic services
test_service_health "Gateway" "8080"
test_service_health "Auth Service" "8086"
test_service_health "Orders Service" "8083"

echo ""
echo "3. Monitoring URLs"
echo "------------------"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin)"
echo "Alertmanager: http://localhost:9093"
echo "MailHog: http://localhost:8025"
echo ""

echo "Basic monitoring test completed!"
