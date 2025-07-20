package com.SpringBoot.InventoryService.Inventory_service.DTO;

public record RoomDTO(
        Long roomId,
        Long hotelId,
        String roomNumber,
        String roomType,
        boolean isAvailable
) {
}
