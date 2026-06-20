package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

public record UpdateRoomRequestDTO(
        RoomType roomType,
        RoomStatus roomStatus
) {
}
