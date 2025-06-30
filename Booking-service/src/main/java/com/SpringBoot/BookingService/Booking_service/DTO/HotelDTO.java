package com.SpringBoot.BookingService.Booking_service.DTO;

public record HotelDTO(
        Long id,
        String name,
        String address,
        String city,
        String contactNumber
) {}
