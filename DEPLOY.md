# Deploy Guide

Ovaj dokument objašnjava kako da deploy-ujete microservices aplikaciju.

## Dostupne Deploy Opcije

### 1. Lokalni Deploy (Development)

```bash
# Jednostavan deploy (build + start)
make deploy

# Ili korak po korak
make build
make up

# Proverite status
make ps

# Pogledajte logove
make logs

# Zaustavite servise
make down
```

### 2. Production Deploy

```bash
# Production deploy sa environment varijablama
cp env.prod.example .env.prod
# Editujte .env.prod sa vašim production vrednostima

# Deploy production verziju
make deploy-prod

# Ili build + deploy
make deploy-prod-build
```

### 3. Health Checks

```bash
# Proverite zdravlje svih servisa
make health

# Testirajte monitoring
make test-monitoring

# Testirajte alerting
make test-alerts
```

## Environment Varijable za Production

Kopirajte `env.prod.example` u `.env.prod` i postavite sledeće vrednosti:

- `JWT_SECRET` - Sigurni JWT secret key
- `AUTH_DB_PASSWORD` - Password za auth bazu
- `CATALOG_DB_PASSWORD` - Password za catalog bazu
- `ORDERS_DB_PASSWORD` - Password za orders bazu
- `PAYMENTS_DB_PASSWORD` - Password za payments bazu
- `RABBITMQ_PASS` - Password za RabbitMQ
- `GRAFANA_ADMIN_PASSWORD` - Password za Grafana

## CI/CD Pipeline

GitHub Actions pipeline automatski:

1. **Test** - Pokreće sve unit testove
2. **Build** - Gradi Docker image-ove
3. **Deploy Dev** - Deploy-uje na development (ako je main branch)
4. **Deploy Prod** - Deploy-uje na production (ako je main branch)

## Service URLs

- **Gateway**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **RabbitMQ**: http://localhost:15672 (guest/guest)
- **MailHog**: http://localhost:8025

## Troubleshooting

### Servisi se ne pokreću
```bash
# Proverite status
make ps

# Pogledajte logove
make logs

# Restart-ujte sve
make down && make up
```

### Database problemi
```bash
# Obrišite sve volume-ove i restart-ujte
make purge
make deploy
```

### Monitoring problemi
```bash
# Testirajte monitoring
make test-monitoring

# Testirajte alerting
make test-alerts
```
