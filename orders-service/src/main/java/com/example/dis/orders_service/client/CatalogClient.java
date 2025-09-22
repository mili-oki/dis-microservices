package com.example.dis.orders_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}
