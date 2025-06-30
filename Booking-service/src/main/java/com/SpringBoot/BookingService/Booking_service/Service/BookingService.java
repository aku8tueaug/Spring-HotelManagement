package com.SpringBoot.BookingService.Booking_service.Service;

import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request);
    BookingResponseDTO cancelBooking(Long bookingId);
    BookingResponseDTO getBookingById(Long bookingId);
    BookingResponseDTO getBookingByStatus(BookingStatus bookingStatus);
    BookingResponseDTO getBookingWithRoomId(Long roomId);
}
