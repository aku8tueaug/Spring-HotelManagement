package com.SpringBoot.BookingService.Booking_service.Client;

import com.SpringBoot.BookingService.Booking_service.DTO.InventoryAvailabilityRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.InventoryReservationRequestDTO;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange

public interface InventoryClient {

    @PostExchange("/inventory/check-availability")
    Boolean checkAvailability(
            @RequestBody InventoryAvailabilityRequestDTO request
    );

    @PatchExchange("/internal/inventory/reserve")
    void reserveInventory(
            @RequestBody InventoryReservationRequestDTO request
    );

    @PatchExchange("/internal/inventory/release")
    void releaseInventory(
            @RequestBody InventoryReservationRequestDTO request
    );

}
