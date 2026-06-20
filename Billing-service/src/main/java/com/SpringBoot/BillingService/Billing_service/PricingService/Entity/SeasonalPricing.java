package com.SpringBoot.BillingService.Billing_service.PricingService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "seasonal_pricings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonalPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Adjustment type is required")
    private String adjustmentType; // e.g. MULTIPLIER, FLAT_ADD, FLAT_SUBTRACT

    @NotNull(message = "Adjustment value is required")
    private BigDecimal adjustmentValue;

    @Version
    private Long version;
}
