package com.SpringBoot.BookingService.Booking_service.Service;

import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Exception.BookingCreationFailedException;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request) throws BookingCreationFailedException;
    BookingResponseDTO cancelBooking(Long bookingId);
    BookingResponseDTO getBooking(Long bookingId);
    List<BookingResponseDTO> getBookingsByStatus(BookingStatus status);
    List<BookingResponseDTO> getBookingsByUserId(Long userId);
    List<BookingResponseDTO> getBookingsByHotelId(Long hotelId);
    BookingResponseDTO checkIn(Long bookingId);
    BookingResponseDTO checkOut(Long bookingId);
}
