package com.example.dis.payments_service.repository;

import com.example.dis.payments_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
