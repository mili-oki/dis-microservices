package com.example.dis.payments_service.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String PAYMENT_NOTIFICATIONS_QUEUE = "q.payment.notifications";
    public static final String ORDERS_PAYMENT_RESULTS_QUEUE = "q.orders.payment-results";

    @Bean
    public Queue paymentNotificationsQueue() {
        return new Queue(PAYMENT_NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    public Queue ordersPaymentResultsQueue() {
        return new Queue(ORDERS_PAYMENT_RESULTS_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(conv);
        return tpl;
    }
}
