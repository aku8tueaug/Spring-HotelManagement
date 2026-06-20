package com.SpringBoot.BillingService.Billing_service.PricingService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.Clients.BookingClient;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.BookingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.*;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RatePlan;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.SeasonalPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.RatePlanRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.SeasonalPricingRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingServiceImplementation implements PricingService {

    private final RatePlanRepository ratePlanRepository;
    private final SeasonalPricingRepository seasonalPricingRepository;
    private final BookingClient bookingClient;

    @Override
    @Transactional(readOnly = true)
    public PriceResponseDTO calculateDynamicPrice(PriceRequestDTO request) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new IllegalArgumentException("Checkout date must be after check-in date");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        long nights = ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate());

        for (int i = 0; i < nights; i++) {
            LocalDate date = request.checkInDate().plusDays(i);

            // Fetch active RatePlan for this night
            RatePlan ratePlan = ratePlanRepository
                    .findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            request.hotelId(), request.roomType(), date, date)
                    .orElseThrow(() -> new IllegalArgumentException("No active RatePlan configured for date: " + date));

            BigDecimal nightPrice = ratePlan.getBasePrice();

            // Fetch active SeasonalPricing adjustments for this night
            List<SeasonalPricing> seasonalPricings = seasonalPricingRepository
                    .findByHotelIdAndRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            request.hotelId(), request.roomType(), date, date);

            for (SeasonalPricing adjustment : seasonalPricings) {
                String type = adjustment.getAdjustmentType().toUpperCase();
                BigDecimal val = adjustment.getAdjustmentValue();
                if ("MULTIPLIER".equals(type) || "PERCENTAGE".equals(type)) {
                    nightPrice = nightPrice.multiply(val);
                } else if ("FLAT_ADD".equals(type)) {
                    nightPrice = nightPrice.add(val);
                } else if ("FLAT_SUBTRACT".equals(type)) {
                    nightPrice = nightPrice.subtract(val);
                }
            }

            subtotal = subtotal.add(nightPrice);
        }

        // Add extra guest charges if guest count > 2 (add $500 per extra guest per night)
        if (request.guestCount() > 2) {
            int extraGuests = request.guestCount() - 2;
            BigDecimal extraCharges = BigDecimal.valueOf(extraGuests)
                    .multiply(BigDecimal.valueOf(500))
                    .multiply(BigDecimal.valueOf(nights));
            subtotal = subtotal.add(extraCharges);
        }

        // Tax Engine Logic
        BigDecimal taxRate = subtotal.compareTo(BigDecimal.valueOf(1000)) >= 0 
                ? BigDecimal.valueOf(0.18) 
                : BigDecimal.valueOf(0.12);
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal finalAmount = subtotal.add(taxAmount);

        return new PriceResponseDTO(subtotal, taxAmount, finalAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPriceForBookingById(Long id) {
        BookingResponseDTO booking = bookingClient.getById(id);
        PriceRequestDTO request = new PriceRequestDTO(
                booking.hotelId(),
                booking.roomType(),
                booking.checkInDate(),
                booking.checkOutDate(),
                booking.guests() != null ? booking.guests().size() : 1
        );
        return calculateDynamicPrice(request).finalAmount();
    }

    @Override
    public RatePlanResponseDTO addRatePlan(RatePlanRequestDTO request) {
        validateRatePlanOverlap(null, request);
        RatePlan ratePlan = RatePlan.builder()
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .basePrice(request.basePrice())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .active(request.active())
                .build();
        ratePlan = ratePlanRepository.save(ratePlan);
        return mapToRatePlanDTO(ratePlan);
    }

    @Override
    public RatePlanResponseDTO updateRatePlan(Long ratePlanId, RatePlanRequestDTO request) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new IllegalArgumentException("RatePlan not found"));
        validateRatePlanOverlap(ratePlanId, request);
        ratePlan.setHotelId(request.hotelId());
        ratePlan.setRoomType(request.roomType());
        ratePlan.setBasePrice(request.basePrice());
        ratePlan.setStartDate(request.startDate());
        ratePlan.setEndDate(request.endDate());
        ratePlan.setActive(request.active());
        ratePlan = ratePlanRepository.save(ratePlan);
        return mapToRatePlanDTO(ratePlan);
    }

    @Override
    public void deleteRatePlan(Long ratePlanId) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new IllegalArgumentException("RatePlan not found"));
        ratePlanRepository.delete(ratePlan);
    }

    @Override
    public SeasonalPricingResponseDTO addSeasonalPricing(SeasonalPricingRequestDTO request) {
        validateSeasonalPricingOverlap(null, request);
        SeasonalPricing seasonalPricing = SeasonalPricing.builder()
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .adjustmentType(request.adjustmentType())
                .adjustmentValue(request.adjustmentValue())
                .build();
        seasonalPricing = seasonalPricingRepository.save(seasonalPricing);
        return mapToSeasonalPricingDTO(seasonalPricing);
    }

    @Override
    public SeasonalPricingResponseDTO updateSeasonalPricing(Long id, SeasonalPricingRequestDTO request) {
        SeasonalPricing seasonalPricing = seasonalPricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeasonalPricing not found"));
        validateSeasonalPricingOverlap(id, request);
        seasonalPricing.setHotelId(request.hotelId());
        seasonalPricing.setRoomType(request.roomType());
        seasonalPricing.setStartDate(request.startDate());
        seasonalPricing.setEndDate(request.endDate());
        seasonalPricing.setAdjustmentType(request.adjustmentType());
        seasonalPricing.setAdjustmentValue(request.adjustmentValue());
        seasonalPricing = seasonalPricingRepository.save(seasonalPricing);
        return mapToSeasonalPricingDTO(seasonalPricing);
    }

    @Override
    public void deleteSeasonalPricing(Long id) {
        SeasonalPricing seasonalPricing = seasonalPricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeasonalPricing not found"));
        seasonalPricingRepository.delete(seasonalPricing);
    }

    private void validateRatePlanOverlap(Long ratePlanId, RatePlanRequestDTO request) {
        List<RatePlan> existingPlans = ratePlanRepository.findByHotelIdAndRoomType(request.hotelId(), request.roomType());
        for (RatePlan plan : existingPlans) {
            if (plan.getActive() && (ratePlanId == null || !plan.getRatePlanId().equals(ratePlanId))) {
                if (request.startDate().isBefore(plan.getEndDate().plusDays(1)) 
                        && request.endDate().isAfter(plan.getStartDate().minusDays(1))) {
                    throw new IllegalArgumentException("Overlap detected with existing active RatePlan ID: " + plan.getRatePlanId());
                }
            }
        }
    }

    private void validateSeasonalPricingOverlap(Long id, SeasonalPricingRequestDTO request) {
        List<SeasonalPricing> existingSeasonals = seasonalPricingRepository.findByHotelIdAndRoomType(request.hotelId(), request.roomType());
        for (SeasonalPricing pricing : existingSeasonals) {
            if (id == null || !pricing.getId().equals(id)) {
                if (request.startDate().isBefore(pricing.getEndDate().plusDays(1)) 
                        && request.endDate().isAfter(pricing.getStartDate().minusDays(1))) {
                    throw new IllegalArgumentException("Overlap detected with existing SeasonalPricing ID: " + pricing.getId());
                }
            }
        }
    }

    private RatePlanResponseDTO mapToRatePlanDTO(RatePlan ratePlan) {
        return new RatePlanResponseDTO(
                ratePlan.getRatePlanId(),
                ratePlan.getHotelId(),
                ratePlan.getRoomType(),
                ratePlan.getBasePrice(),
                ratePlan.getStartDate(),
                ratePlan.getEndDate(),
                ratePlan.getActive()
        );
    }

    private SeasonalPricingResponseDTO mapToSeasonalPricingDTO(SeasonalPricing sp) {
        return new SeasonalPricingResponseDTO(
                sp.getId(),
                sp.getHotelId(),
                sp.getRoomType(),
                sp.getStartDate(),
                sp.getEndDate(),
                sp.getAdjustmentType(),
                sp.getAdjustmentValue()
        );
    }
}
