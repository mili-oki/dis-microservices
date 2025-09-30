#!/bin/bash

# Test script za osnovno alarmiranje
echo "Testing Basic Alerting Setup"
echo "============================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo "1. Testing Alertmanager"
echo "----------------------"

# Test Alertmanager
echo -n "Testing Alertmanager... "
response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:9093" 2>/dev/null)
if [ "$response" = "200" ] || [ "$response" = "302" ]; then
    echo -e "${GREEN}✓ PASS${NC} (HTTP $response)"
else
    echo -e "${RED}✗ FAIL${NC} (HTTP $response)"
fi

echo ""
echo "2. Testing MailHog"
echo "------------------"

# Test MailHog
echo -n "Testing MailHog... "
response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8025" 2>/dev/null)
if [ "$response" = "200" ] || [ "$response" = "302" ]; then
    echo -e "${GREEN}✓ PASS${NC} (HTTP $response)"
else
    echo -e "${RED}✗ FAIL${NC} (HTTP $response)"
fi

echo ""
echo "3. Testing Prometheus Alerts"
echo "----------------------------"

# Test Prometheus alerts
echo -n "Checking Prometheus alerts... "
alerts_response=$(curl -s "http://localhost:9090/api/v1/alerts" 2>/dev/null)
if echo "$alerts_response" | grep -q '"status":"success"'; then
    echo -e "${GREEN}✓ ALERTS CONFIGURED${NC}"
else
    echo -e "${YELLOW}⚠ NO ALERTS FOUND${NC}"
fi

echo ""
echo "4. Alerting URLs"
echo "----------------"
echo "Alertmanager: http://localhost:9093"
echo "MailHog: http://localhost:8025"
echo "Prometheus Alerts: http://localhost:9090/alerts"
echo ""

echo "Basic alerting test completed!"
