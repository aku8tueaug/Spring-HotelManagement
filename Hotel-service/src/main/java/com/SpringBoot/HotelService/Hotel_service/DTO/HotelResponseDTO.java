package com.SpringBoot.HotelService.Hotel_service.DTO;

import java.time.LocalDateTime;

public record HotelResponseDTO(
        Long id,
        String name,
        AddressDTO address,
        Integer rating,
        String description,
        String contactNumber,
        String email,
        String imageUrl,
        Boolean active,
        LocalDateTime createdAt
) {}
