package com.example.dis.orders_service.controller;

import com.example.dis.orders_service.model.Order;
import com.example.dis.orders_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestParam Long productId, @RequestParam Integer quantity) {
        return orderService.createOrder(productId, quantity);
    }

    @PutMapping("/{id}/quantity")
    public Order updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        return orderService.updateQuantity(id, quantity);
    }

    @PutMapping("/{id}/confirm")
    public Order confirm(@PathVariable Long id) {
        return orderService.confirm(id);
    }

    @PutMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
