package com.SpringBoot.BillingService.Billing_service.PaymentService.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod; // e.g. CREDIT_CARD, UPI, CASH
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}
