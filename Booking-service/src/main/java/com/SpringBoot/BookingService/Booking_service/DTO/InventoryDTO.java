package com.SpringBoot.BookingService.Booking_service.DTO;


import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;

public record InventoryDTO(
        Long inventoryId,
        Long hotelId,
        RoomType roomType,
        Long roomId,
        Integer totalRooms,
        Integer availableRooms
) {}