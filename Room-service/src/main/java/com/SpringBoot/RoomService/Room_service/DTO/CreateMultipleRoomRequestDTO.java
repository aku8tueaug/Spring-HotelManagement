package com.SpringBoot.RoomService.Room_service.DTO;

import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;



public record CreateMultipleRoomRequestDTO(

        @NotNull(message = "Hotel ID is required")
        Long hotelId,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        @NotNull(message = "Room status is required")
        RoomStatus roomStatus,

        @NotNull(message = "Floor number is required")
        @Min(value = 0, message = "Floor number must be greater than and equals to 0")
        Integer floorNumber,

        @NotNull(message = "Starting room number is required")
        @Min(value = 1, message = "Starting room number must be greater than 0")
        Integer startRoomNumber,

        @NotNull(message = "Room count is required")
        @Min(value = 1, message = "At least one room must be created")
        Integer roomCount

) {

}
