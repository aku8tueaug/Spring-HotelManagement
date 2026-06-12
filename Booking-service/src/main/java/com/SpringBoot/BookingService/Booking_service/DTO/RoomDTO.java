package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

public record RoomDTO(
        Long hotelId,
        RoomType roomType,
        String roomNumber

) {
}
