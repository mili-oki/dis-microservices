package com.example.dis.catalog_service.service;

import com.example.dis.catalog_service.dto.ProductRequest;
import com.example.dis.catalog_service.dto.ProductResponse;
import com.example.dis.catalog_service.model.Product;
import com.example.dis.catalog_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(String q) {
        var items = (q == null || q.isBlank()) ? repo.findAll() : repo.findByNameContainingIgnoreCase(q);
        return items.stream().map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        var p = new Product();
        p.setName(req.getName());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        var saved = repo.save(p);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock());
    }

    @Transactional(readOnly = true)
    public Product require(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        var p = require(id);
        p.setName(req.getName());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        var saved = repo.save(p);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice(), saved.getStock());
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Product not found: " + id);
        repo.deleteById(id);
    }
}
