package com.SpringBoot.BillingService.Billing_service.PricingService.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Weekend multiplier is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Weekend multiplier must be non-negative")
    private BigDecimal weekendMultiplier;

    @NotNull(message = "Seasonal multiplier is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Seasonal multiplier must be non-negative")
    private BigDecimal seasonalMultiplier;

}
