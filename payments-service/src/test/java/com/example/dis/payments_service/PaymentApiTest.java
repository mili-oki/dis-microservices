package com.example.dis.payments_service;

import com.example.dis.payments_service.client.NotificationsClient;
import com.example.dis.payments_service.client.OrderDto;
import com.example.dis.payments_service.client.OrderStatus;
import com.example.dis.payments_service.client.OrdersClient;
import com.example.dis.payments_service.messaging.PaymentEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test",
        // H2 datasource
        "spring.datasource.url=jdbc:h2:mem:paytest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        // isključi service discovery/feign
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.openfeign.enabled=false",
        // ne diži rabbit listenere
        "spring.rabbitmq.listener.simple.auto-startup=false"
    }
)
class PaymentApiTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @MockBean OrdersClient ordersClient;
    @MockBean NotificationsClient notificationsClient;

    // ⬇️ najbitnije: mock publish-era da ne vuče Rabbit bean-ove
    @MockBean PaymentEventPublisher publisher;

    @Test
    void createPayment_endpoint_returns_201_and_body() {
        OrderDto dto = new OrderDto();
        dto.setId(7L);
        dto.setStatus(OrderStatus.PENDING);
        when(ordersClient.getOrder(7L)).thenReturn(dto);

        String url = "http://localhost:" + port + "/payments?orderId=7&amount=99.99";
        ResponseEntity<Map> res = rest.postForEntity(URI.create(url), null, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        Map body = res.getBody();
        assertThat(body.get("orderId")).isIn(7, 7L);
        assertThat(body.get("amount").toString()).isEqualTo("99.99");
        assertThat(body.get("status").toString()).isNotBlank(); // npr. SUCCESS
    }

    @Test
    void getAll_returns_200() {
        ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/payments", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
