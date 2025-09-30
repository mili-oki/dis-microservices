package com.example.dis.orders_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
@CircuitBreaker(name = "catalog-service")
@Retry(name = "catalog-service")
@TimeLimiter(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}
