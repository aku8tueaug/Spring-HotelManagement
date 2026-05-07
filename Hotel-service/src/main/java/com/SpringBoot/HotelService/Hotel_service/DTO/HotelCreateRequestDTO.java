package com.SpringBoot.HotelService.Hotel_service.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record HotelCreateRequestDTO(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Valid AddressDTO address, // added @valid for nested Validation
        @NotNull @Min(1) @Max(5) Integer rating,
        String description,
        @Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number")
        String contactNumber,
        String email,
        String imageUrl
) {}