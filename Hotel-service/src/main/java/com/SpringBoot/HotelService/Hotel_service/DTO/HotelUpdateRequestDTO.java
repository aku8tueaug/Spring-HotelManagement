package com.SpringBoot.HotelService.Hotel_service.DTO;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelUpdateRequestDTO {

    @Size(max = 150)
    private String name;

    @Valid
    private AddressDTO address;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 500)
    private String description;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number")
    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String imageUrl;

    private Boolean active;
}
