package com.example.dis.payments_service.client;

import java.math.BigDecimal;

public class PaymentNotification {
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String message;
    
    public PaymentNotification() {}
    public PaymentNotification(Long orderId, BigDecimal amount, String status, String message) {
        this.orderId = orderId; this.amount = amount; this.status = status; this.message = message;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
