package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

import java.time.LocalDate;

public record BookingResponseDTO(
        Long bookingId,
        Long hotelId,
        String  roomId,
        RoomType roomType,
        String guestName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BookingStatus bookingStatus,
        Double price
) {}
