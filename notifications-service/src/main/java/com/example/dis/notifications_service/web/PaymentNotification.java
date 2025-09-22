package com.example.dis.notifications_service.web;

import java.math.BigDecimal;

public class PaymentNotification {
    private Long orderId;
    private BigDecimal amount;
    private String status; // SUCCESS / FAILED / ...
    private String message;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
