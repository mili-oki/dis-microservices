package com.example.dis.payments_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notifications-service")
public interface NotificationsClient {

    @PostMapping("/notifications/payment")
    void notifyPayment(@RequestBody PaymentNotification notification);
}
