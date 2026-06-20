package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import java.time.LocalDate;

public record InventoryResponseDTO(
        Long inventoryId,
        Long hotelId,
        RoomType roomType,
        LocalDate inventoryDate,
        Integer totalRooms,
        Integer reservedRooms,
        Integer blockedRooms,
        Integer availableRooms
) {
}
