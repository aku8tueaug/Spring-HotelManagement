package com.SpringBoot.InventoryService.Inventory_service.DTO;

import com.SpringBoot.InventoryService.Inventory_service.Entity.InventoryAction;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;

public record RoomInventoryEvent(
        String operationId,
        Long hotelId,
        RoomType roomType,
        Integer roomCount,
        InventoryAction action
) {}
