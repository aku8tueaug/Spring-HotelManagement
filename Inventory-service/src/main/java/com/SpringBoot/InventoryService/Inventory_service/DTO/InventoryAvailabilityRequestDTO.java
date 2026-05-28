package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import java.time.LocalDate;

public record InventoryAvailabilityRequestDTO(
        Long hotelId,
        RoomType roomType,
        LocalDate startDate,
        LocalDate endDate,
        Integer requiredRooms
) {
}
