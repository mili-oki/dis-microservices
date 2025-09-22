package com.example.dis.orders_service.repository;

import com.example.dis.orders_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
