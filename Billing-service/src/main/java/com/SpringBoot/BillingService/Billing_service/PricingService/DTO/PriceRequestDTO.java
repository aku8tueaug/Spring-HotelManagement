package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;

public record PriceRequestDTO(
        Long hotelId,
        RoomType roomType,
        int numberOfGuests,
        int stayLengthInDays,
        boolean isWeekend,
        boolean isSeasonal
) {
}
