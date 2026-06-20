package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

import java.time.LocalDate;

public record InventoryAvailabilityRequestDTO(
        Long hotelId,
        RoomType roomType,
        LocalDate startDate,
        LocalDate endDate,
        Integer requiredRooms
) {}
