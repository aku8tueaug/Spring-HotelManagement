package com.SpringBoot.BookingService.Booking_service.Controller;

import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {

        return ResponseEntity.ok(
                bookingService.createBooking(request)
        );
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId)
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBooking(bookingId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByStatus(
            @PathVariable BookingStatus status) {

        return ResponseEntity.ok(
                bookingService.getBookingsByStatus(status)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByUserId(userId)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByHotelId(
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByHotelId(hotelId)
        );
    }
    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<BookingResponseDTO> checkIn(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(
                bookingService.checkIn(bookingId)
        );
    }

    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<BookingResponseDTO> checkOut(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(
                bookingService.checkOut(bookingId)
        );
    }

}
