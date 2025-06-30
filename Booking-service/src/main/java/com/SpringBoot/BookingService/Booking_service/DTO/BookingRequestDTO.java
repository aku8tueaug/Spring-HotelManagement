package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
public record BookingRequestDTO(
        @NotNull(message = "Hotel ID is required") Long hotelId,
        @NotNull(message = "Room ID is required") Long roomId,
        @NotNull(message = "Room type is required") RoomType roomType,
        @NotBlank(message = "Guest name is required") String guestName,
        @NotNull(message = "Check-in date is required") LocalDate checkInDate,
        @NotNull(message = "Check-out date is required") LocalDate checkOutDate
) {}