package com.example.dis.catalog_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products",
       indexes = {
         @Index(name = "idx_products_name", columnList = "name")
       })
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String name;

    @Column(nullable=false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock; // koliko komada na lageru

    // getteri/setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
