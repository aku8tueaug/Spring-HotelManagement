package com.SpringBoot.BillingService.Billing_service.PricingService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.*;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/pricing/calculate")
    public ResponseEntity<PriceResponseDTO> calculatePrice(@RequestBody PriceRequestDTO priceRequestDTO) {
        PriceResponseDTO response = pricingService.calculateDynamicPrice(priceRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/pricing/amount/booking/{id}")
    public ResponseEntity<BigDecimal> getPriceByBookingId(@PathVariable("id") Long id) {
        BigDecimal amount = pricingService.getPriceForBookingById(id);
        return ResponseEntity.ok(amount);
    }

    @PostMapping("/rate-plans")
    public ResponseEntity<RatePlanResponseDTO> addRatePlan(@RequestBody RatePlanRequestDTO request) {
        RatePlanResponseDTO response = pricingService.addRatePlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/rate-plans/{id}")
    public ResponseEntity<RatePlanResponseDTO> updateRatePlan(
            @PathVariable Long id,
            @RequestBody RatePlanRequestDTO request) {
        RatePlanResponseDTO response = pricingService.updateRatePlan(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/rate-plans/{id}")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long id) {
        pricingService.deleteRatePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seasonal-pricings")
    public ResponseEntity<SeasonalPricingResponseDTO> addSeasonalPricing(@RequestBody SeasonalPricingRequestDTO request) {
        SeasonalPricingResponseDTO response = pricingService.addSeasonalPricing(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/seasonal-pricings/{id}")
    public ResponseEntity<SeasonalPricingResponseDTO> updateSeasonalPricing(
            @PathVariable Long id,
            @RequestBody SeasonalPricingRequestDTO request) {
        SeasonalPricingResponseDTO response = pricingService.updateSeasonalPricing(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/seasonal-pricings/{id}")
    public ResponseEntity<Void> deleteSeasonalPricing(@PathVariable Long id) {
        pricingService.deleteSeasonalPricing(id);
        return ResponseEntity.noContent().build();
    }
}
