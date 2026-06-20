package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Validation.DateRangeRequest;
import com.SpringBoot.InventoryService.Inventory_service.Validation.DateRangeValid;

import java.time.LocalDate;

@DateRangeValid
public record InventoryReservationRequestDTO(
        Long hotelId,
        RoomType roomType,
        LocalDate startDate,
        LocalDate endDate,
        Integer roomCount
) implements DateRangeRequest {
}
