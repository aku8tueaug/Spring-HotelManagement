package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingRequestDTO(
        @NotNull(message = "Hotel ID is required") Long hotelId,
        @NotNull(message = "Room type is required") RoomType roomType,
        @NotNull Long userId,
        @Min(1)
        @NotNull Integer roomCount,
        @Min(1)
        @NotNull
        List<GuestDTO> guests,
        @FutureOrPresent
        @NotNull(message = "Check-in date is required") LocalDateTime checkInDateTime,

        @Future
        @NotNull(message = "Check-out date is required") LocalDateTime checkOutDateTime
) { }