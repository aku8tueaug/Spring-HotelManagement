package com.SpringBoot.BookingService.Booking_service.DTO;

public record GuestDTO(
        String fullName,
        String idType,
        String idNumber,
        Integer age
) {
}
