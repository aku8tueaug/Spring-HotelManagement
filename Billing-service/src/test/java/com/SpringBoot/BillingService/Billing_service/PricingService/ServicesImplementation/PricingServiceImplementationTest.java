package com.SpringBoot.BillingService.Billing_service.PricingService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.Clients.BookingClient;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.BookingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PersonDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.*;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.BookingStatus;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RatePlan;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.SeasonalPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.RatePlanRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.SeasonalPricingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceImplementationTest {

    @Mock
    private RatePlanRepository ratePlanRepository;

    @Mock
    private SeasonalPricingRepository seasonalPricingRepository;

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private PricingServiceImplementation pricingService;

    private RatePlan createSampleRatePlan(Long planId, BigDecimal basePrice, LocalDate start, LocalDate end, boolean active) {
        return RatePlan.builder()
                .ratePlanId(planId)
                .hotelId(1L)
                .roomType(RoomType.STANDARD)
                .basePrice(basePrice)
                .startDate(start)
                .endDate(end)
                .active(active)
                .build();
    }

    private SeasonalPricing createSampleSeasonalPricing(Long id, String type, BigDecimal val, LocalDate start, LocalDate end) {
        return SeasonalPricing.builder()
                .id(id)
                .hotelId(1L)
                .roomType(RoomType.STANDARD)
                .startDate(start)
                .endDate(end)
                .adjustmentType(type)
                .adjustmentValue(val)
                .build();
    }

    // ==========================================
    // 1. calculateDynamicPrice() Tests
    // ==========================================

    @Test
    void testCalculateDynamicPrice_CheckoutBeforeCheckin_ThrowsIllegalArgumentException() {
        PriceRequestDTO request = new PriceRequestDTO(
                1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().minusDays(1), 2
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculateDynamicPrice(request);
        });
        assertEquals("Checkout date must be after check-in date", ex.getMessage());
    }

    @Test
    void testCalculateDynamicPrice_NoRatePlanConfigured_ThrowsIllegalArgumentException() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        PriceRequestDTO request = new PriceRequestDTO(1L, RoomType.STANDARD, checkIn, checkOut, 2);

        when(ratePlanRepository.findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculateDynamicPrice(request);
        });
        assertTrue(ex.getMessage().contains("No active RatePlan configured for date"));
    }

    @Test
    void testCalculateDynamicPrice_StandardCase_WithLowTaxBracket() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        PriceRequestDTO request = new PriceRequestDTO(1L, RoomType.STANDARD, checkIn, checkOut, 2);
        RatePlan ratePlan = createSampleRatePlan(101L, BigDecimal.valueOf(500), checkIn.minusDays(5), checkIn.plusDays(5), true);

        when(ratePlanRepository.findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Optional.of(ratePlan));

        when(seasonalPricingRepository.findByHotelIdAndRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.emptyList());

        PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

        assertNotNull(response);
        // Subtotal = 500. Under 1000, so tax rate is 12% ($60). Total = 560.
        assertEquals(0, BigDecimal.valueOf(500).compareTo(response.subtotal()));
        assertEquals(0, BigDecimal.valueOf(60).compareTo(response.taxAmount()));
        assertEquals(0, BigDecimal.valueOf(560).compareTo(response.finalAmount()));
    }

    @Test
    void testCalculateDynamicPrice_WithSeasonalPricingAdjustments() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        PriceRequestDTO request = new PriceRequestDTO(1L, RoomType.STANDARD, checkIn, checkOut, 2);
        RatePlan ratePlan = createSampleRatePlan(101L, BigDecimal.valueOf(1000), checkIn.minusDays(5), checkIn.plusDays(5), true);

        SeasonalPricing spMultiplier = createSampleSeasonalPricing(201L, "MULTIPLIER", BigDecimal.valueOf(1.5), checkIn, checkOut);
        SeasonalPricing spFlatAdd = createSampleSeasonalPricing(202L, "FLAT_ADD", BigDecimal.valueOf(100), checkIn, checkOut);
        SeasonalPricing spFlatSubtract = createSampleSeasonalPricing(203L, "FLAT_SUBTRACT", BigDecimal.valueOf(50), checkIn, checkOut);

        when(ratePlanRepository.findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Optional.of(ratePlan));

        when(seasonalPricingRepository.findByHotelIdAndRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(spMultiplier, spFlatAdd, spFlatSubtract));

        PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

        assertNotNull(response);
        // Night base: 1000.
        // Multiplier: 1000 * 1.5 = 1500.
        // Flat add: 1500 + 100 = 1600.
        // Flat subtract: 1600 - 50 = 1550.
        // Tax bracket: >= 1000 (18% tax on 1550 = 279). Total = 1829.
        assertEquals(0, BigDecimal.valueOf(1550).compareTo(response.subtotal()));
        assertEquals(0, BigDecimal.valueOf(279).compareTo(response.taxAmount()));
        assertEquals(0, BigDecimal.valueOf(1829).compareTo(response.finalAmount()));
    }

    @Test
    void testCalculateDynamicPrice_WithExtraGuests_Success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2); // 2 nights
        // 4 guests total -> 2 extra guests
        PriceRequestDTO request = new PriceRequestDTO(1L, RoomType.STANDARD, checkIn, checkOut, 4);
        RatePlan ratePlan = createSampleRatePlan(101L, BigDecimal.valueOf(300), checkIn.minusDays(5), checkIn.plusDays(5), true);

        when(ratePlanRepository.findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Optional.of(ratePlan));

        when(seasonalPricingRepository.findByHotelIdAndRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.emptyList());

        PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

        assertNotNull(response);
        // Nights = 2. Subtotal room charge = 300 * 2 = 600.
        // Extra guests charge = 2 guests * $500 * 2 nights = 2000.
        // Total subtotal = 2600. Tax >= 1000 is 18% of 2600 = 468. Final = 3068.
        assertEquals(0, BigDecimal.valueOf(2600).compareTo(response.subtotal()));
        assertEquals(0, BigDecimal.valueOf(468).compareTo(response.taxAmount()));
        assertEquals(0, BigDecimal.valueOf(3068).compareTo(response.finalAmount()));
    }

    // ==========================================
    // 2. getPriceForBookingById() Tests
    // ==========================================

    @Test
    void testGetPriceForBookingById_Success() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        BookingResponseDTO booking = new BookingResponseDTO(
                10L, 1L, "101", RoomType.STANDARD, "John Doe", checkIn, checkOut,
                BookingStatus.CONFIRMED, 560.0, List.of(
                        new PersonDTO(1L, "G1", LocalDate.now(), "123456", "Address", "1234567890"),
                        new PersonDTO(2L, "G2", LocalDate.now(), "123456", "Address", "1234567890")
                )
        );
        RatePlan ratePlan = createSampleRatePlan(101L, BigDecimal.valueOf(500), checkIn.minusDays(5), checkIn.plusDays(5), true);

        when(bookingClient.getById(10L)).thenReturn(booking);
        when(ratePlanRepository.findByHotelIdAndRoomTypeAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Optional.of(ratePlan));

        BigDecimal finalPrice = pricingService.getPriceForBookingById(10L);

        assertEquals(0, BigDecimal.valueOf(560).compareTo(finalPrice));
    }

    // ==========================================
    // 3. RatePlan Management & Overlaps
    // ==========================================

    @Test
    void testAddRatePlan_OverlapDetected_ThrowsIllegalArgumentException() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(10);
        RatePlanRequestDTO request = new RatePlanRequestDTO(1L, RoomType.STANDARD, BigDecimal.valueOf(500), start, end, true);

        RatePlan existing = createSampleRatePlan(101L, BigDecimal.valueOf(500), start.minusDays(2), start.plusDays(5), true);

        when(ratePlanRepository.findByHotelIdAndRoomType(1L, RoomType.STANDARD)).thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.addRatePlan(request);
        });
        assertTrue(ex.getMessage().contains("Overlap detected with existing active RatePlan ID"));
    }

    @Test
    void testAddRatePlan_Success() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(10);
        RatePlanRequestDTO request = new RatePlanRequestDTO(1L, RoomType.STANDARD, BigDecimal.valueOf(500), start, end, true);
        RatePlan saved = createSampleRatePlan(102L, BigDecimal.valueOf(500), start, end, true);

        when(ratePlanRepository.findByHotelIdAndRoomType(1L, RoomType.STANDARD)).thenReturn(Collections.emptyList());
        when(ratePlanRepository.save(any(RatePlan.class))).thenReturn(saved);

        RatePlanResponseDTO response = pricingService.addRatePlan(request);

        assertNotNull(response);
        assertEquals(102L, response.ratePlanId());
        verify(ratePlanRepository).save(any(RatePlan.class));
    }

    @Test
    void testUpdateRatePlan_NotFound_ThrowsIllegalArgumentException() {
        RatePlanRequestDTO request = new RatePlanRequestDTO(1L, RoomType.STANDARD, BigDecimal.valueOf(500), LocalDate.now(), LocalDate.now().plusDays(5), true);
        when(ratePlanRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.updateRatePlan(999L, request);
        });
        assertEquals("RatePlan not found", ex.getMessage());
    }

    @Test
    void testDeleteRatePlan_Success() {
        RatePlan plan = createSampleRatePlan(101L, BigDecimal.valueOf(500), LocalDate.now(), LocalDate.now().plusDays(5), true);
        when(ratePlanRepository.findById(101L)).thenReturn(Optional.of(plan));

        pricingService.deleteRatePlan(101L);

        verify(ratePlanRepository).delete(plan);
    }

    // ==========================================
    // 4. SeasonalPricing Management & Overlaps
    // ==========================================

    @Test
    void testAddSeasonalPricing_OverlapDetected_ThrowsIllegalArgumentException() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(10);
        SeasonalPricingRequestDTO request = new SeasonalPricingRequestDTO(1L, RoomType.STANDARD, start, end, "MULTIPLIER", BigDecimal.valueOf(1.5));

        SeasonalPricing existing = createSampleSeasonalPricing(201L, "FLAT_ADD", BigDecimal.valueOf(100), start.minusDays(2), start.plusDays(5));

        when(seasonalPricingRepository.findByHotelIdAndRoomType(1L, RoomType.STANDARD)).thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.addSeasonalPricing(request);
        });
        assertTrue(ex.getMessage().contains("Overlap detected with existing SeasonalPricing ID"));
    }

    @Test
    void testUpdateSeasonalPricing_NotFound_ThrowsIllegalArgumentException() {
        SeasonalPricingRequestDTO request = new SeasonalPricingRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(5), "FLAT_ADD", BigDecimal.valueOf(100));
        when(seasonalPricingRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.updateSeasonalPricing(999L, request);
        });
        assertEquals("SeasonalPricing not found", ex.getMessage());
    }
}
