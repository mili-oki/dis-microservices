package com.example.dis.payments_service.web;

import com.example.dis.payments_service.model.Payment;
import com.example.dis.payments_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @GetMapping
    public List<Payment> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment pay(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
        return service.pay(orderId, amount);
    }
}
