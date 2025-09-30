# 1)  Kratak pregled sistema
Sistem predstavlja mini verziju e-commerce domen i sastavljen je od sledećih komponenti (lokalni portovi u zagradama):

**API Gateway**  (8080) — ulazna tačka za front-end i klijente; rutira zahteve ka mikroservisima, validira JWT (gateway.yml).

**Auth Service** (8086) - prijava korisnika i izdavanje JWT tokena; 

**Catalog Service** (8082) - upravljanje proizvodima (CRUD, cena, dostupnost); određuje dostupnost artikla i cene.

**Orders Service** (8083)- kreira i menja narudžbine; validira artikle sihronizovano preko Catalog-a ; očekuje ishod plaćanja da bi pratio status narudžbine ( sihronizacija sa Payments-om)

**Payments Service** (8084) — transakcije plaćanja; vodi evidenciju o plaćanjima, inicira naplatu i vraća status; nakon obrade objavljuje događaje u RabbitMQ ( slanje notifikacija ).

**Notifications Service** (8085) — prima događaje o ishodu plaćanja i (u realnom sistemu) šalje/beleži obaveštenja (logove u kontejneru).

**RabbitMQ** – posrednik za asinhornu razmenu poruka (topic exchange + queue-ovi).

# 2) Poslovna logika po domenu

## 2.1 Auth Service 
Obezbeđuje registraciju korisnika i prijavu preko JWT tokena, koji se zatim koriste za autentifikaciju i autorizaciju u ostatku mikroservisnog sistema.

    - Registracija korisnika 
        - proverava da li je korisničko ime jedinstveno; lozinka se hešira koristeći BCrypt; default  rola je ROLE_USER
    - Prijava korisnika ( Login) 
        - proverava da li korisčniko ime postoji; validacija lozinke poređenjem sa BCrypt hešom; ako su kredencijali ispravni - generiše se JWT token sa ID korisnika, roles i vremenom važenja
        - token se vraća klijentu i koristi za pristup servisima

```java 
UserAccount Entity
id: Long (Primary Key, Auto-generated)
username: String (Unique, Required)
passwordHash: String (BCrypt hashed password)
roles: String (Default: "ROLE_USER")
createdAt: OffsetDateTime (Auto-set on creation)
```

## 2.2. Catalog Service
Obezbeđuje CRUD operacije za upravljanje katalogom proizvoda. Implementirani su API za kreiranje proizovda, lista i pretraga proizvoda, dobavljanje proizvoda po ID, izmena proizvoda, brisanje proizvoda

```java 
Product Entity 
id: Long (Primary Key, Auto-generated)
name: String (Required, Max 120 chars, Indexed for search)
price: BigDecimal (Required, Precision 12, Scale 2, Min 0.00)
stock: Integer (Required, Min 0, represents inventory quantity)
```

## 2.3 Orders Service
Upravlja porudžbinama uz funkcionalnosti: kreiranje, čitanje, izmena količine, potvrda, otkazivanje, brisanje; validacija sa katalozima i obrada rezultata plaćanja preko poruka.

```java
Order Entity
id: Long (Primary Key, Auto-generated)
productId: Long (Reference to catalog-service product)
quantity: Integer (Ordered quantity)
status: OrderStatus (PENDING, CONFIRMED, CANCELLED, PAYED)
``` 

```java
Order Status Enum
PENDING
CONFIRMED 
CANCELLED
PAYED  
```

**(Ne)Dozvoljenje promene statusa**
Dozvoljeno:
PENDING → CONFIRMED
PENDING → CANCELLED
CONFIRMED → PAYED (preko rezultata plaćanja - Payments service)
PAYMENT FAILED → PENDING (rollback iz toka plaćanja - Payments service)

Zabranjeno:
CONFIRMED → CANCELLED
PAYED → * 
CANCELLED → * 

#### Ključna poslovna pravila
1. Validacija proizvoda i zaliha: pre kreiranja/izmene količine obavezna provera preko catalog-service
2. Izmene su dozvoljene samo u PENDING: izmena količine ili potvrda.
3. Brisanje porudžbine: dozvoljeno za ne-CONFIRMED porudžbine (biznis pravilo).
4. Plaćanje je event-driven: PaymentResultEvent menja status: SUCCESS → PAYED, FAILED → PENDING.


#### Integracija sa Catalog Service i Payment Service - Tipični tokovi - skraćeno

    Kreiranje porudžbine
    - Klijent → POST /orders → catalog-service validacija → Order{PENDING} → 201 Created.

    Potvrda porudžbine
    - Klijent → PUT /orders/{id}/confirm (samo PENDING) → CONFIRMED.

    Tok plaćanja (event-driven)
    - Payment-Service → MQ (PaymentResultEvent) → Orders listener ( ažurira status) :
    - SUCCESS → ... → PAYED
    - FAILED → ... → PENDING (može novi pokušaj plaćanja)
    


## 2.4 Payments Service
Vrši obradu plaćanja porudžbina zajedno sa drugim servisima: : validira porudžbinu, kreira zapis plaćanja, potvrđuje porudžbinu, emituje događaje (event-ove) i šalje notifikacije.

```java
Payment Entity
id: Long (Primary Key, Auto-generated)
orderId: Long (Reference to orders-service order)
amount: BigDecimal (Payment amount, precision 12, scale 2)
status: PaymentStatus (PENDING, SUCCESS, FAILED)
createdAt: OffsetDateTime (Payment timestamp)
```

```java
Payment Status Enum
PENDING 
SUCCESS 
FAILED
```

#### (Ne)Dozvoljenje promene statusa
- Stanje mora biti veće od 0 (amount > 0)
- Porudžbina mora da postoji i bude u PENDING statusu

#### Integracija sa Orders-Service, Notifications-Service i RabbitMQ

    Orders-Service (Feign OrdersClient)
    - getOrder(Long id) – validacija postojanja i statusa
    - confirm(Long id) – posle uspešnog plaćanja: PENDING → CONFIRMED

    Notifications-Service (Feign NotificationsClient)
    - notifyPayment(PaymentNotification) – direktno HTTP obaveštenje (user/channel)

    RabbitMQ (Publisher PaymentEventPublisher)
    - q.orders.payment-results – payload za promenu stanja porudžbine
    - q.payment.notifications – log notifikacije

## 2.5 Notifications Service

Prijem i isporuka obaveštenja o plaćanju preko dva kanala: HTTP (sihronizovano, direktnim poziv sa payments-service) i RabbitMQ (asihronizovano). Ovaj servise je bez baze, fokusiran je na isporuku poruka i audit log-ove. Subscribuje se na ‘payment.*’ događaje i šalje/beleži notifikacije.

```java 
PaymentNotification
orderId: Long (Reference to order)
amount: BigDecimal (Payment amount)
status: String (SUCCESS, FAILED, etc.)
message: String (Notification message)
``` 
#### Ključna poslovna pravila

1) HTTP notfikacije
    - Prima PaymentNotification preko HTTP-a iz payments-service, logovanje poruka

2) RabbitMQ notifikacije
    - Queue: q.payment.notifications. payments-service objavi poruku na q.payment.notifications
    - Listener: @RabbitListener (npr. PaymentEventListener) prima poruku i loguje detalje
    - Routing keys: payment.succeeded i payment.failed


# 3) Glavni tokovi (end-to-end)

1. Registracija/Login korisnika preko Auth-Service
2. Korisnik pregleda proizvode iz Catalog-Service i kreira kreira narudžbinu u Orders-Service
3. Korisnik pokreće plaćanje narudžbine(orderId) u Payments-Service
4. Payments upisuje transakciju q.payment.notifications i određuje ishod SUCCESS/FAILED
5. Notifications obrađuje event i ispisuje log
6. Orders sluša q.orders.payment-results i ažurira status narudžbine (PAID / FAILED).


# 4) Docker Setup i Pokretanje

## 4.1 Pokretanje sa Docker Compose

### Potrebni zahtevi
- Docker Desktop
- Docker Compose
- Minimum 4GB RAM
- Portovi: 8080, 8082-8086, 8761, 8888, 9090, 9093, 3000, 8025, 15672

### Brzo pokretanje
```bash
# Kloniranje repozitorijuma
git clone <repository-url>
cd dis-microservices2

# Pokretanje svih servisa
docker-compose up -d

# Proveravanje statusa
docker-compose ps

# Pregled logova
docker-compose logs -f
```

### Makefile komande
```bash
# Build svih servisa
make build

# Pokretanje servisa
make up

# Zaustavljanje servisa
make down

# Pregled logova
make logs

# Status servisa
make ps

# Potpuno čišćenje
make purge
```

### Pokretanje sa monitoring-om
```bash
# Pokretanje svih servisa uključujući monitoring
docker-compose up -d

# Osnovni monitoring pristup
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/admin)
```

### Zaustavljanje servisa
```bash
# Zaustavljanje svih servisa
docker-compose down

# Zaustavljanje sa brisanjem volumena
docker-compose down -v

# Brisanje svih slika
docker-compose down --rmi all
```

### Troubleshooting
```bash
# Pregled logova određenog servisa
docker-compose logs <service-name>

# Restart servisa
docker-compose restart <service-name>

# Rebuild servisa
docker-compose build <service-name>
docker-compose up -d <service-name>

# Proveravanje resursa
docker stats
```

## 4.3 Development Setup

### Lokalni development
```bash
# Pokretanje samo infrastrukture (DB, RabbitMQ, Config Server)
docker-compose up -d postgres-auth postgres-catalog postgres-orders postgres-payments rabbitmq config-server discovery-service

# Pokretanje servisa lokalno (u IDE-u)
# - Auth Service: 8086
# - Catalog Service: 8082  
# - Orders Service: 8083
# - Payments Service: 8084
# - Notifications Service: 8085
# - Gateway: 8080
```

### Hot reload za development
```bash
# Restart samo jednog servisa
docker-compose restart <service-name>

# Rebuild i restart
docker-compose build <service-name> && docker-compose up -d <service-name>
```

## 4.2 Portovi i Servisi

| Servis | Port | Opis |
|--------|------|------|
| Gateway | 8080 | API Gateway |
| Auth Service | 8086 | Autentifikacija |
| Catalog Service | 8082 | Katalog proizvoda |
| Orders Service | 8083 | Upravljanje narudžbinama |
| Payments Service | 8084 | Plaćanja |
| Notifications Service | 8085 | Notifikacije |
| Discovery Service | 8761 | Eureka |
| Config Server | 8888 | Konfiguracija |
| Prometheus | 9090 | Metrije |
| Grafana | 3000 | Dashboard |
| Alertmanager | 9093 | Alarmi |
| MailHog | 8025 | Test email |
| RabbitMQ | 15672 | Message broker UI |

## 4.4 Minimalni zahtevi

- **RAM**: 4GB
- **CPU**: 2 cores  
- **Disk**: 5GB slobodnog prostora
- **Docker**: 20.10+ verzija

# 5) Kako testirati lokalno (primeri cURL, preko Gateway-a) 

### 1. Registracija/Login korisnika preko Auth-Service
#### [STEP 1] Registration
```bash
curl -X POST http://localhost:8080/auth-service/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### [STEP 2]  Login
```bash
#copy token result from response
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
  "token": "eyJhbGciOiJIUzI1N..."
}
```

### 2. Korisnik pregleda proizvode iz Catalog-Service i kreira kreira narudžbinu u Orders-Service

#### [STEP 3] Get All Products
```bash
curl -i -X GET http://localhost:8080/catalog-service/products
```

#### [STEP 4] Create Order
```bash
curl -i -X POST "http://localhost:8080/orders-service/orders?productId=1&quantity=2" -H "Authorization: Bearer eyJhbGciOiJIUzI1N...."
```

#### [STEP 5] List Order
```bash
curl -i http://localhost:8080/orders-service/orders 
```

#### [STEP 6] Confirm Order
```bash
curl -X PUT http://localhost:8080/orders-service/orders/1/confirm 
```

### 3. Korisnik pokreće plaćanje narudžbine(orderId) u Payments-Service

#### [STEP 6] Process Payment
```bash
curl -i -X POST "http://localhost:8080/payments-service/payments?orderId=1&amount=99.99" -H "Authorization: Bearer eyJhbGciOiJIUzI1N...."
```

### 4. Notifications obrađuje event i ispisuje log

#### [STEP 7] Check Notification logs inside container
```bash
docker-compose logs --tail=100 notifications-service orders-service
```

**Expected Response:**
```
[NOTIFY] Payment event received: orderId=18, amount=99.99, status=SUCCESS, msg=Payment processed and order confirmed
[NOTIFY:MQ] Payment event: orderId=18, amount=99.99, status=SUCCESS, msg=Payment captured 
[ORDERS:MQ] Payment result received: orderId=18, status=SUCCESS, msg=Payment captured
```

# 6) Osnovni Monitoring

Sistem uključuje osnovni monitoring stack za praćenje performansi mikroservisa.

## 6.1 Monitoring Servisi

- **Prometheus** (9090) - prikupljanje metrika
- **Grafana** (3000) - osnovni dashboard  
- **Alertmanager** (9093) - upravljanje alarmima
- **MailHog** (8025) - test email server

## 6.2 Pristup Monitoring-u

```bash
# Prometheus - metrije
http://localhost:9090

# Grafana - dashboard (admin/admin)
http://localhost:3000

# Alertmanager - alarmi
http://localhost:9093

# MailHog - test email
http://localhost:8025
```

## 6.3 Osnovno Alarmiranje

Sistem uključuje osnovne alarme:

- **Service Down** - kada servis nije dostupan
- **Circuit Breaker Open** - kada circuit breaker je otvoren  
- **High Error Rate** - kada error rate > 10%

Email notifikacije se šalju na `admin@microservices.local` preko MailHog-a.

### Testiranje alarmiranja
```bash
# Test osnovnog alarmiranja
./scripts/test-alerts.sh

# Pregled email notifikacija
# Otvori http://localhost:8025 u browseru
```

## 6.4 Osnovni Endpoints

Svaki servis ima osnovne monitoring endpoint-e:

- `/actuator/health` - health check
- `/actuator/prometheus` - metrije za Prometheus