package com.SpringBoot.BookingService.Booking_service.Controller;

import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(
            @Valid @RequestBody BookingRequestDTO request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancel(
            @PathVariable("id") Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getById(
            @PathVariable("id") Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @GetMapping("bookingByStatus/{bookingStatus}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingByStatus(@PathVariable("bookingStatus") String bookingStatus)
    {
       return ResponseEntity.ok(bookingService.getBookingsByStatus(bookingStatus));
    }

    @GetMapping("/bookingByRoomNumber/{roomNumber}")
    public ResponseEntity<BookingResponseDTO> getBookingByRoomNumber(
            @PathVariable("roomNumber") String roomNumber) {
        return ResponseEntity.ok(bookingService.getBookingByRoomNumber(roomNumber));
    }
    @PutMapping("/additionalCharges")
    public ResponseEntity<BookingResponseDTO> addAdditionalCharges(
            @RequestParam Long bookingId,
            @RequestParam BigDecimal extraCharges) {

        BookingResponseDTO updatedBooking = bookingService.addAdditionalCharges(bookingId, extraCharges);
        return ResponseEntity.ok(updatedBooking);
    }
}
