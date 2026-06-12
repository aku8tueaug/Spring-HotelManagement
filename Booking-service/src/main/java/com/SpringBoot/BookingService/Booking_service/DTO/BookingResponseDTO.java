package com.SpringBoot.BookingService.Booking_service.DTO;



import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDTO(
        Long bookingId,
        Long hotelId,
        RoomType roomType,
        Long userId,
        Integer roomCount,
        List<GuestDTO> guests,
        LocalDate bookingDate,
        LocalDateTime checkInDateTime,
        LocalDateTime checkOutDateTime,
        BookingStatus status
) {}
