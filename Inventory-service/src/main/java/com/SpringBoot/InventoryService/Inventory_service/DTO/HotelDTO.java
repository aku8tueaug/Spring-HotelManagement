package com.SpringBoot.InventoryService.Inventory_service.DTO;

public record HotelDTO(
        Long id,
        String name,
        String address,
        String city,
        String contactNumber
) {}
