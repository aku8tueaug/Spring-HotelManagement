package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import java.math.BigDecimal;

public record PriceResponseDTO(
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal finalAmount
) {
}
