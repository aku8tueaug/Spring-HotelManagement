package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

public record InventoryAdjustmentRequestDTO(
        Long hotelId,
        RoomType roomType,
        Integer count) {
}
