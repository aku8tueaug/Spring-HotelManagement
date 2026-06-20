package com.SpringBoot.BillingService.Billing_service.PricingService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratePlanId;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull
    private Boolean active;

    @Version
    private Long version;
}
