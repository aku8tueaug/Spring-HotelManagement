package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import java.time.LocalDate;

public record PriceRequestDTO(
        Long hotelId,
        RoomType roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount
) {
}
