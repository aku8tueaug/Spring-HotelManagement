package com.SpringBoot.HotelService.Hotel_service.Mapper;

import com.SpringBoot.HotelService.Hotel_service.DTO.AddressDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelCreateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelResponseDTO;
import com.SpringBoot.HotelService.Hotel_service.Entity.Address;
import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;

public class HotelMapper {

    // DTO → Entity (Create)
    public static Hotel toEntity(HotelCreateRequestDTO dto) {

        Address address = Address.builder()
                .street(dto.address().street())
                .city(dto.address().city())
                .state(dto.address().state())
                .country(dto.address().country())
                .zipCode(dto.address().zipCode())
                .build();

        return Hotel.builder()
                .name(dto.name())
                .rating(dto.rating())
                .description(dto.description())
                .contactNumber(dto.contactNumber())
                .email(dto.email())
                .imageUrl(dto.imageUrl())
                .address(address)
                .active(true)
                .build();
    }

    // Entity → Response DTO
    public static HotelResponseDTO toResponseDTO(Hotel hotel) {

        AddressDTO addressDTO = new AddressDTO(
                hotel.getAddress().getStreet(),
                hotel.getAddress().getCity(),
                hotel.getAddress().getState(),
                hotel.getAddress().getCountry(),
                hotel.getAddress().getZipCode()
        );

        return new HotelResponseDTO(
                hotel.getId(),
                hotel.getName(),
                addressDTO,
                hotel.getRating(),
                hotel.getDescription(),
                hotel.getContactNumber(),
                hotel.getEmail(),
                hotel.getImageUrl(),
                hotel.getActive(),
                hotel.getCreatedAt()
        );
    }
}
