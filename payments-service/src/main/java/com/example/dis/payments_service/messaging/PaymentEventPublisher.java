package com.example.dis.payments_service.messaging;

import com.example.dis.payments_service.client.PaymentNotification; // koristimo postojeći DTO
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.dis.payments_service.messaging.RabbitConfig.PAYMENT_NOTIFICATIONS_QUEUE;
import static com.example.dis.payments_service.messaging.RabbitConfig.ORDERS_PAYMENT_RESULTS_QUEUE;

@Component
public class PaymentEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishToNotifications(PaymentNotification event) {
        rabbitTemplate.convertAndSend(PAYMENT_NOTIFICATIONS_QUEUE, event);
    }

    public void publishToOrders(PaymentNotification event) {
        rabbitTemplate.convertAndSend(ORDERS_PAYMENT_RESULTS_QUEUE, event);
    }
}
