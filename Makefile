build:
	docker compose build

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

ps:
	docker compose ps

purge:
	docker compose down -v --remove-orphans

# Deploy commands
deploy: build up
	@echo "Deployment completed! Services are starting up..."
	@echo "Check status with: make ps"
	@echo "View logs with: make logs"

deploy-prod:
	docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

deploy-prod-build:
	docker compose -f docker-compose.yml -f docker-compose.prod.yml build
	docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Health check
health:
	@echo "Checking service health..."
	@./scripts/check-health.sh

# Test commands
test-monitoring:
	@./scripts/test-monitoring.sh

test-alerts:
	@./scripts/test-alerts.sh

