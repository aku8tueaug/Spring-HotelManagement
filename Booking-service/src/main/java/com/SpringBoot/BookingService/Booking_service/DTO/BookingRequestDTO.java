package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.Person;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BookingRequestDTO(
        @NotNull(message = "Hotel ID is required") Long hotelId,
        @NotNull(message = "Room ID is required") String roomNumber,
        @NotNull(message = "Room type is required") RoomType roomType,
        @NotNull(message = "List of Guest required")List<Person> guests,
        String guestName,
        @NotNull(message = "Check-in date is required") LocalDate checkInDate,
        @NotNull(message = "Check-out date is required") LocalDate checkOutDate
) {
    public BookingRequestDTO {
        if (guests == null || guests.isEmpty()) {
            throw new IllegalArgumentException("Guest list cannot be null or empty");
        }
        guestName = guests.get(0).getName();
    }
}