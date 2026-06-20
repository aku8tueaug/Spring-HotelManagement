package com.SpringBoot.BillingService.Billing_service.PricingService.Services;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.*;
import java.math.BigDecimal;

public interface PricingService {
    PriceResponseDTO calculateDynamicPrice(PriceRequestDTO request);
    BigDecimal getPriceForBookingById(Long id);

    RatePlanResponseDTO addRatePlan(RatePlanRequestDTO request);
    RatePlanResponseDTO updateRatePlan(Long ratePlanId, RatePlanRequestDTO request);
    void deleteRatePlan(Long ratePlanId);

    SeasonalPricingResponseDTO addSeasonalPricing(SeasonalPricingRequestDTO request);
    SeasonalPricingResponseDTO updateSeasonalPricing(Long id, SeasonalPricingRequestDTO request);
    void deleteSeasonalPricing(Long id);
}
