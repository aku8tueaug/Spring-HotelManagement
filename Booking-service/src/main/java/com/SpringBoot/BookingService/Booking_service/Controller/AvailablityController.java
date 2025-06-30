package com.SpringBoot.BookingService.Booking_service.Controller;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import com.SpringBoot.BookingService.Booking_service.Service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

public class AvailablityController {
    private final AvailabilityService availabilityService;

    public AvailablityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<Map<RoomType, Integer>> checkAvailability(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                availabilityService.findAvailableByType(hotelId, from, to)
        );
    }
}
