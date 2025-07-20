package com.SpringBoot.BillingService.Billing_service.PricingService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.Clients.BookingClient;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.BookingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.RoomPricingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.RoomPricingRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class PricingServiceImplementation implements PricingService {

    public final RoomPricingRepository  roomPricingRepository;
    public final BookingClient bookingClient;


    public PricingServiceImplementation( RoomPricingRepository roomPricingRepository, BookingClient bookingClient)
    {
        this.roomPricingRepository = roomPricingRepository;
        this.bookingClient = bookingClient;
    }

    @Override
    public PriceResponseDTO calculateDynamicPrice(PriceRequestDTO priceRequest) {
        RoomPricing pricing = roomPricingRepository.findByHotelIdAndRoomType(
                        priceRequest.hotelId(), priceRequest.roomType())
                .orElseThrow(() -> new IllegalArgumentException("Pricing not configured."));

        BigDecimal price = pricing.getBasePrice();

        if (priceRequest.isWeekend()) {
            price =price.add( price.multiply(pricing.getWeekendMultiplier()));
        }

        if (priceRequest.isSeasonal()) {
            price = price.add( price.multiply(pricing.getSeasonalMultiplier()));
        }

        if (priceRequest.numberOfGuests() > 2) {
            int extraGuest = priceRequest.numberOfGuests() -2;
            BigDecimal extraGuestCharges = BigDecimal.valueOf(extraGuest).multiply(BigDecimal.valueOf(500));
            price = price.add(extraGuestCharges); // extra guest fee
        }

        price = price.multiply(BigDecimal.valueOf(priceRequest.stayLengthInDays()));

        return new PriceResponseDTO(priceRequest.hotelId(), priceRequest.roomType(), price);

    }



    @Override
    public BigDecimal getPriceForBookingById(Long id) {
        BookingResponseDTO bookingResponseDTO = bookingClient.getById(id);
        PriceRequestDTO priceRequestDTO = mapToPriceRequestDTO(bookingResponseDTO);
        PriceResponseDTO priceResponseDTO = calculateDynamicPrice(priceRequestDTO);
        return priceResponseDTO.finalPrice();
    }


 // CRUD OPS for Room Pricing
    @Override
    public RoomPricingResponseDTO addPricing(RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomType roomType = RoomType.valueOf(roomPricingRequestDTO.roomType().toUpperCase());
        Optional<RoomPricing> roomPricing = roomPricingRepository.findByHotelIdAndRoomType(
                roomPricingRequestDTO.hotelId(), roomType);

        if (roomPricing.isPresent()) {
            throw new RuntimeException("The price for this roomType is already maintained. Please update instead.");
        }

        RoomPricing newPricing = mapToRoomPricing(roomPricingRequestDTO);
        newPricing = roomPricingRepository.save(newPricing);
        return mapToRoomPricingDTO(newPricing);
    }

    @Override
    public RoomPricingResponseDTO updatePricing(RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomType roomType = RoomType.valueOf(roomPricingRequestDTO.roomType().toUpperCase());
        Optional<RoomPricing> existing = roomPricingRepository.findByHotelIdAndRoomType(
                roomPricingRequestDTO.hotelId(), roomType);

        if (existing.isEmpty()) {
            throw new RuntimeException("The price for this roomType is not maintained. Please add instead.");
        }

        RoomPricing updated = mapToRoomPricing(roomPricingRequestDTO);
        updated.setId(existing.get().getId()); // preserve existing ID
        updated = roomPricingRepository.save(updated);
        return mapToRoomPricingDTO(updated);
    }

    @Override
    public RoomPricingResponseDTO deletePricing(RoomPricingRequestDTO roomPricingRequestDTO) {
        RoomType roomType = RoomType.valueOf(roomPricingRequestDTO.roomType().toUpperCase());
        Optional<RoomPricing> existing = roomPricingRepository.findByHotelIdAndRoomType(
                roomPricingRequestDTO.hotelId(), roomType);

        if (existing.isEmpty()) {
            throw new RuntimeException("No pricing found for the given hotel and room type.");
        }

        roomPricingRepository.delete(existing.get());
        return mapToRoomPricingDTO(existing.get()); // return deleted pricing
    }


    //helper method
    private RoomPricing mapToRoomPricing(RoomPricingRequestDTO roomPricingRequestDTO)
    {
        return RoomPricing.builder()
                .hotelId(roomPricingRequestDTO.hotelId())
                .roomType(RoomType.valueOf(roomPricingRequestDTO.roomType().toUpperCase()))
                .basePrice(BigDecimal.valueOf(roomPricingRequestDTO.basePrice()))
                .weekendMultiplier(BigDecimal.valueOf(roomPricingRequestDTO.weekendMultiplier()))
                .seasonalMultiplier(BigDecimal.valueOf(roomPricingRequestDTO.seasonalMultiplier()))
                .build();
    }
    private RoomPricingResponseDTO mapToRoomPricingDTO(RoomPricing roomPricing)
    {
        return new RoomPricingResponseDTO(
                roomPricing.getId(),
                roomPricing.getHotelId(),
                roomPricing.getRoomType(),
                roomPricing.getBasePrice(),
                roomPricing.getWeekendMultiplier(),
                roomPricing.getSeasonalMultiplier()
        );
    }
    private PriceRequestDTO mapToPriceRequestDTO(BookingResponseDTO booking )
    {
        boolean isWeekendBooking = isWeekendBooking(booking.checkInDate(),booking.checkOutDate());
        boolean isSeasonal = isSeasonalBooking(booking.checkInDate(),booking.checkOutDate());
        Long noOfDays = ChronoUnit.DAYS.between(booking.checkInDate(),booking.checkOutDate());

        return new PriceRequestDTO(
                booking.hotelId(),
                booking.roomType(),
                booking.guests().size(),
                noOfDays.intValue(),
                isWeekendBooking,
                isSeasonal
        );
    }
    private boolean isWeekendBooking(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.getDayOfWeek().getValue() >= 6 ||
                checkOut.getDayOfWeek().getValue() >= 6;
    }

    private boolean isSeasonalBooking(LocalDate checkIn, LocalDate checkOut) {
        // Example: July-August considered seasonal
        Month checkInMonth = checkIn.getMonth();
        Month checkOutMonth = checkOut.getMonth();
        return (checkInMonth == Month.JULY ||
                checkInMonth == Month.AUGUST ||
                checkInMonth == Month.NOVEMBER||
                checkInMonth == Month.DECEMBER)
                ||
                (checkOutMonth == Month.JULY ||
                        checkOutMonth == Month.AUGUST ||
                        checkOutMonth == Month.NOVEMBER||
                        checkOutMonth == Month.DECEMBER
                );
    }





}
