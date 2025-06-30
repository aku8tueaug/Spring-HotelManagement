package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

public record AvailabilityResponseDTO(
        Long hotelId,
        RoomType roomType,
        int availableCount
) {}
