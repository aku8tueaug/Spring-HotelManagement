package com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import java.math.BigDecimal;

public record InvoiceResponseDTO(
        Long invoiceId,
        Long bookingId,
        String invoiceNumber,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        InvoiceStatus status
) {
}
