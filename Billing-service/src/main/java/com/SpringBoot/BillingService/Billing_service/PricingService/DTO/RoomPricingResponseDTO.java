package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;

import java.math.BigDecimal;

public record RoomPricingResponseDTO(
        Long id,
        Long hotelId,
        RoomType roomType,
        BigDecimal basePrice,
        BigDecimal weekendMultiplier,
        BigDecimal seasonalMultiplier
) {
}
