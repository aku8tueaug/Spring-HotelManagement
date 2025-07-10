package com.SpringBoot.BookingService.Booking_service.Controller;


import com.SpringBoot.BookingService.Booking_service.DTO.AvailabilityResponseDTO;
import com.SpringBoot.BookingService.Booking_service.Service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // 1. Get available rooms by hotel ID
    @GetMapping("/hotel/{hotelId}")
    public List<AvailabilityResponseDTO> getAvailableRoomsByHotelId(@PathVariable("hotelId") Long hotelId) {
        return availabilityService.getAvailableRoomByHotelId(hotelId);
    }

    // 2. Get available rooms by room type (across all hotels)
    @GetMapping("/roomType/{roomType}")
    public List<AvailabilityResponseDTO> getAvailableRoomsByRoomType(@PathVariable("roomType") String roomType) {
        return availabilityService.getAvailableRoomsByRoomTypes(roomType);
    }

    // 3. Get available rooms for a specific hotel and room type
    @GetMapping("/hotel/{hotelId}/roomType/{roomType}")
    public AvailabilityResponseDTO getAvailableRoomsByHotelIdAndRoomType(
            @PathVariable Long hotelId,
            @PathVariable String roomType
    ) {
        return availabilityService.getAvailableRoomsByHotelIdAndRoomType(hotelId, roomType);
    }

    // 4. Check availability between two dates
    @GetMapping("/check")
    public AvailabilityResponseDTO checkAvailabilityBetweenDates(
            @RequestParam Long hotelId,
            @RequestParam String roomType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) {
        return availabilityService.checkRoomAvailabilityBetweenDates(
                hotelId, roomType, checkInDate, checkOutDate
        );
    }
}

