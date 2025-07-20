package com.SpringBoot.BillingService.Billing_service.PricingService.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomPricingRequestDTO(
        @NotNull(message = "Hotel ID is required")
        Long hotelId,

        @NotBlank(message = "Room type is required")
        String roomType,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
        double basePrice,

        @NotNull(message = "Weekend multiplier is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Weekend multiplier must be non-negative")
        double weekendMultiplier,

        @NotNull(message = "Seasonal multiplier is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Seasonal multiplier must be non-negative")
        double seasonalMultiplier

) {
}
