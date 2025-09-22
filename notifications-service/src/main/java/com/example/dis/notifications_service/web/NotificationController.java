package com.example.dis.notifications_service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void onPayment(@RequestBody PaymentNotification payload) {
        // Minimalno: samo zaloguj šta je stiglo
        log.info("[NOTIFY] Payment event received: orderId={}, amount={}, status={}, msg={}",
                payload.getOrderId(), payload.getAmount(), payload.getStatus(), payload.getMessage());
        // Ovde bi išlo slanje emaila/SMS/Webhook, itd.
    }
}
