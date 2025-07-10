package com.SpringBoot.BookingService.Booking_service.Service;

import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request);
    BookingResponseDTO cancelBooking(Long bookingId);
    BookingResponseDTO getBookingById(Long bookingId);
    List<BookingResponseDTO> getBookingsByStatus(String bookingStatus);
//    BookingResponseDTO getBookingWithRoomId(Long roomId);
    BookingResponseDTO getBookingByRoomNumber(String roomNumber);
}
