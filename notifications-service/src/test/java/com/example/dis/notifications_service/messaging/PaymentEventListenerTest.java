package com.example.dis.notifications_service.messaging;

import com.example.dis.notifications_service.web.PaymentNotification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;

class PaymentEventListenerTest {

    @Test
    void onPayment_does_not_throw_for_valid_payload() {
        PaymentEventListener listener = new PaymentEventListener();

        PaymentNotification pn = new PaymentNotification();
        pn.setOrderId(42L);
        pn.setAmount(new BigDecimal("10.50"));
        pn.setStatus("SUCCESS");
        pn.setMessage("mq");

        assertThatCode(() -> listener.onPayment(pn)).doesNotThrowAnyException();
    }
}
