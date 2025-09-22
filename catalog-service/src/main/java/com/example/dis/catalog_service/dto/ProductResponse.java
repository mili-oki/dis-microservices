package com.example.dis.catalog_service.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;

    public ProductResponse(Long id, String name, BigDecimal price, Integer stock) {
        this.id = id; this.name = name; this.price = price; this.stock = stock;
    }

    // getteri
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
}
