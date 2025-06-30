package com.SpringBoot.RoomService.Room_service.DTO;

public record Hotel(
        Long id,
        String name,
        String address,
        String city,
        String contactNumber
) {}
