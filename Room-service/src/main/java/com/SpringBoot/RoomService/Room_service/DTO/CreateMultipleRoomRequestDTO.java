package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import jakarta.validation.constraints.NotNull;

public record CreateMultipleRoomRequestDTO(
        @NotNull Long HotelId,
        @NotNull String roomType,
        @NotNull Integer floorNumber,
        @NotNull Integer idStart,
        @NotNull int noOfRoomToBeCreate,
        @NotNull boolean isAvailable

) {
}
