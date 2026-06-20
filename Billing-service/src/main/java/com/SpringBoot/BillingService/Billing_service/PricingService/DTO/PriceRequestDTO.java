package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import java.time.LocalDate;

public record PriceRequestDTO(
        Long hotelId,
        RoomType roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount
) {
}
