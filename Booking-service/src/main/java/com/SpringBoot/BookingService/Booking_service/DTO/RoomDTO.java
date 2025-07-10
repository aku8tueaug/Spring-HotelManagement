package com.SpringBoot.BookingService.Booking_service.DTO;

public record RoomDTO(
        Long roomId,
        Long hotelId,
        String roomNumber,
        String roomType,
        Double basePrice,
        boolean available
) {
}
