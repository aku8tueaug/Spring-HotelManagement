package com.SpringBoot.BillingService.Billing_service.PricingService.Services;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceResponseDTO;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingResponseDTO;

import java.math.BigDecimal;


public interface PricingService {
    public PriceResponseDTO calculateDynamicPrice(PriceRequestDTO request);
//    public BigDecimal getPriceForBooking(PriceRequestDTO request);
    public RoomPricingResponseDTO addPricing(RoomPricingRequestDTO roomPricing);
    public RoomPricingResponseDTO updatePricing(RoomPricingRequestDTO roomPricing);
    public RoomPricingResponseDTO deletePricing(RoomPricingRequestDTO roomPricing);
    public BigDecimal getPriceForBookingById(Long id);
}
