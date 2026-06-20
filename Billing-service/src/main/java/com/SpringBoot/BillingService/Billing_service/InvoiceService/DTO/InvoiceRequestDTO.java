package com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO;

import java.math.BigDecimal;

public record InvoiceRequestDTO(
        Long bookingId,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount
) {
}
