package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;

import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long paymentId,
        Long bookingId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime paymentDate
) {
}
