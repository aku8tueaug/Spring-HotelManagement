package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;



import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.BookingStatus;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;

import java.time.LocalDate;
import java.util.List;

public record BookingResponseDTO(
        Long bookingId,
        Long hotelId,
        String  roomId,
        RoomType roomType,
        String guestName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BookingStatus bookingStatus,
        Double price,
        List<PersonDTO> guests
) {}
