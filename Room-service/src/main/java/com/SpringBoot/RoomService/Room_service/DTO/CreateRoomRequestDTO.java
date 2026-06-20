package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequestDTO(
        @NotNull(message = "Hotel ID is required")
        Long hotelId,

        @NotBlank(message = "Room Number is required")
        String roomNumber,

        @NotNull(message = "Room Type must be specified")
        RoomType roomType,

        @NotNull RoomStatus roomStatus
        ) {
}
