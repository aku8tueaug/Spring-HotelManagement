package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RatePlanResponseDTO(
        Long ratePlanId,
        Long hotelId,
        RoomType roomType,
        BigDecimal basePrice,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active
) {
}
