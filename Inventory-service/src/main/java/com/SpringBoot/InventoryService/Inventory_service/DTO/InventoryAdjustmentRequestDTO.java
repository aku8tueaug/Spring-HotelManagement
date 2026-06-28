package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;

public record InventoryAdjustmentRequestDTO(
        Long hotelId,
        RoomType roomType,
        Integer count
) {
}
