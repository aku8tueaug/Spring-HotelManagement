package com.SpringBoot.BookingService.Booking_service.DTO;



import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.Person;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingResponseDTO(
        Long bookingId,
        Long hotelId,
        String  roomId,
        RoomType roomType,
        String guestName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BookingStatus bookingStatus,
        BigDecimal price,
        List<Person> guests
) {}
