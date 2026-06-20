package com.SpringBoot.HotelService.Hotel_service.Service;

import com.SpringBoot.HotelService.Hotel_service.DTO.HotelCreateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelResponseDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelUpdateRequestDTO;
import org.springframework.data.domain.Page;

public interface HotelService {
    HotelResponseDTO createHotel(HotelCreateRequestDTO request);

    HotelResponseDTO updateHotel(Long id, HotelUpdateRequestDTO request);

    HotelResponseDTO getHotelById(Long id);

    Page<HotelResponseDTO> getAllHotels(int page, int size, String sortBy, String direction);

    HotelResponseDTO deleteHotelById(Long id);

    HotelResponseDTO reactivateHotelById(Long id);

}