package com.SpringBoot.RoomService.Room_service.DTO;


import com.SpringBoot.RoomService.Room_service.Entity.InventoryAction;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

public record RoomInventoryEvent(
        String operationId,
        Long hotelId,
        RoomType newRoomType,
        RoomType oldRoomType,
        Integer roomCount,
        InventoryAction action
) {}
