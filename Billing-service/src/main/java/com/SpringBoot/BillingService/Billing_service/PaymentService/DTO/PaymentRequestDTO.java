package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        Long invoiceId,
        BigDecimal amount,
        String paymentMethod,
        String transactionReference
) {
}
