package com.SpringBoot.BookingService.Booking_service.DTO;

import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import jakarta.validation.constraints.NotNull;

public record AdjustmentDTO(
        @NotNull Long hotelId,
        @NotNull RoomType roomType,
        @NotNull int adjustment
) {
}
