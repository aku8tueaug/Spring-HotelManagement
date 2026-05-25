package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;

public record ResponseRoomDTO(
        Long roomId,
        Long hotelId,
        String roomNumber,
        RoomType roomType,
        RoomStatus roomStatus) {


}
