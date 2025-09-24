package com.example.dis.orders_service.messaging;

import com.example.dis.orders_service.model.OrderStatus;
import com.example.dis.orders_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.dis.orders_service.messaging.RabbitConfig.ORDERS_PAYMENT_RESULTS_QUEUE;

@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);
    private final OrderService orderService;

    public PaymentResultListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Transactional
    @RabbitListener(queues = ORDERS_PAYMENT_RESULTS_QUEUE)
    public void onPaymentResult(@Payload PaymentResultEvent payload) {
        log.info("[ORDERS:MQ] Payment result received: orderId={}, status={}, msg={}",
                payload.getOrderId(), payload.getStatus(), payload.getMessage());

        // Minimalni zahtev: FAILED -> vrati u PENDING
        if ("FAILED".equalsIgnoreCase(payload.getStatus())) {
            orderService.updateStatus(payload.getOrderId(), OrderStatus.PENDING);
            log.info("[ORDERS:MQ] Order {} set to PENDING (payment failed)", payload.getOrderId());
            return;
        }

        // (Opcionalno) SUCCESS -> PAYED
        if ("SUCCESS".equalsIgnoreCase(payload.getStatus())) {
            orderService.updateStatus(payload.getOrderId(), OrderStatus.PAYED);
            log.info("[ORDERS:MQ] Order {} set to PAYED (payment success)", payload.getOrderId());
        }
    }
}
