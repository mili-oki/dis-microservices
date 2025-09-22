package com.example.dis.payments_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "orders-service")
public interface OrdersClient {

    @GetMapping("/orders/{id}")
    OrderDto getOrder(@PathVariable("id") Long id);

    @PutMapping("/orders/{id}/confirm")
    OrderDto confirm(@PathVariable("id") Long id);
}
