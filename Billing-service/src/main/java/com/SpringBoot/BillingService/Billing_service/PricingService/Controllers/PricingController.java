package com.SpringBoot.BillingService.Billing_service.PricingService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/Pricing")
public class PricingController {
    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Calculate final price dynamically based on booking inputs
     */
    @PostMapping("/calculate")
    public ResponseEntity<PriceResponseDTO> calculatePrice(@RequestBody PriceRequestDTO priceRequestDTO) {
        PriceResponseDTO response = pricingService.calculateDynamicPrice(priceRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    /**
//     * Fetch only price amount (used by PaymentService)
//     */
//    @PostMapping("/amount")
//    public ResponseEntity<BigDecimal> getPriceForBooking(@RequestBody PriceRequestDTO priceRequestDTO) {
//        BigDecimal amount = pricingService.getPriceForBooking(priceRequestDTO);
//        return new ResponseEntity<>(amount, HttpStatus.OK);
//    }
// Get price using booking ID (calls BookingClient internally)
        @GetMapping("/amount/booking/{id}")
        public ResponseEntity<BigDecimal> getPriceByBookingId(@PathVariable("id") Long id) {
            BigDecimal amount = pricingService.getPriceForBookingById(id);
            return ResponseEntity.ok(amount);
        }
    /**
     * Add new room pricing
     */
    @PostMapping("/room")
    public ResponseEntity<RoomPricingResponseDTO> addPricing(@RequestBody RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomPricingResponseDTO response = pricingService.addPricing(roomPricingRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update room pricing
     */
    @PutMapping("/room")
    public ResponseEntity<RoomPricingResponseDTO> updatePricing(@RequestBody RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomPricingResponseDTO response = pricingService.updatePricing(roomPricingRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete room pricing
     */
    @DeleteMapping("/room")
    public ResponseEntity<RoomPricingResponseDTO> deletePricing(@RequestBody RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomPricingResponseDTO response = pricingService.deletePricing(roomPricingRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
