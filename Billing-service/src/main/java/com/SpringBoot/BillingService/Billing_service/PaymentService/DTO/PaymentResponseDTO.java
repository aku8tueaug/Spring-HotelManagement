package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;

import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long paymentId,
        Long invoiceId,
        BigDecimal amount,
        String paymentMethod,
        String transactionReference,
        PaymentStatus status,
        LocalDateTime paymentDate
) {
}
