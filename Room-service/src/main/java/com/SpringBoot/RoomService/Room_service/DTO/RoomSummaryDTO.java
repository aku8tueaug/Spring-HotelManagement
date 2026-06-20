package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

public record RoomSummaryDTO(
        Long hotelId,
        RoomType roomType,
        String roomNumber
) {
}
