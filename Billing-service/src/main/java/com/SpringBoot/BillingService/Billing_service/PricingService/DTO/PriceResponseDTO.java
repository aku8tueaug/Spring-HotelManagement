package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;

import java.math.BigDecimal;

public record PriceResponseDTO(
        Long hotelId,
        RoomType roomType,
        BigDecimal finalPrice
) {
}
