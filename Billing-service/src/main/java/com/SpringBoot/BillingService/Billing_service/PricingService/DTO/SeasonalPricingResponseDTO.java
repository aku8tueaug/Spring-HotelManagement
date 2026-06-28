package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.AdjustmentType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SeasonalPricingResponseDTO(
        Long id,
        Long hotelId,
        RoomType roomType,
        LocalDate startDate,
        LocalDate endDate,
        AdjustmentType adjustmentType,
        BigDecimal adjustmentValue
) {
}
