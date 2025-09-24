package com.example.dis.notifications_service.messaging;

import com.example.dis.notifications_service.web.PaymentNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.example.dis.notifications_service.messaging.RabbitConfig.PAYMENT_NOTIFICATIONS_QUEUE;

@Component
public class PaymentEventListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    @RabbitListener(queues = PAYMENT_NOTIFICATIONS_QUEUE)
    public void onPayment(@Payload PaymentNotification payload) {
        log.info("[NOTIFY:MQ] Payment event: orderId={}, amount={}, status={}, msg={}",
                payload.getOrderId(), payload.getAmount(), payload.getStatus(), payload.getMessage());
        // Ovde kasnije ubaci e-mail/SMS/WebPush...
    }
}
