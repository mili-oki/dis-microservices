package com.example.dis.notifications_service.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String PAYMENT_NOTIFICATIONS_QUEUE = "q.payment.notifications";

    @Bean
    public Queue paymentNotificationsQueue() {
        return new Queue(PAYMENT_NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
