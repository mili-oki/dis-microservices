package com.example.dis.orders_service.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ORDERS_PAYMENT_RESULTS_QUEUE = "q.orders.payment-results";

    @Bean
    public Queue ordersPaymentResultsQueue() {
        return new Queue(ORDERS_PAYMENT_RESULTS_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
