package com.SpringBoot.HotelService.Hotel_service.ServiceImplementation;

import com.SpringBoot.HotelService.Hotel_service.DTO.HotelCreateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelResponseDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelUpdateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.Entity.Address;
import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import com.SpringBoot.HotelService.Hotel_service.Exception.ResourceNotFoundException;
import com.SpringBoot.HotelService.Hotel_service.Mapper.HotelMapper;
import com.SpringBoot.HotelService.Hotel_service.Repository.HotelRepository;
import com.SpringBoot.HotelService.Hotel_service.Service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public HotelResponseDTO createHotel(HotelCreateRequestDTO request) {
        log.info("Creating hotel with name: {}", request.name());
        Hotel hotel = HotelMapper.toEntity(request);

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with id: {}", savedHotel.getId());
        return HotelMapper.toResponseDTO(savedHotel);
    }

    @Override
    public HotelResponseDTO updateHotel(Long id, HotelUpdateRequestDTO dto) {
        log.info("Updating hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {  log.error("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found with id: " + id);
                });

        // Partial update logic
        if (dto.getName() != null) {
            hotel.setName(dto.getName());
        }

        if (dto.getRating() != null) {
            hotel.setRating(dto.getRating());
        }

        if (dto.getDescription() != null) {
            hotel.setDescription(dto.getDescription());
        }

        if (dto.getContactNumber() != null) {
            hotel.setContactNumber(dto.getContactNumber());
        }

        if (dto.getEmail() != null) {
            hotel.setEmail(dto.getEmail());
        }

        if (dto.getImageUrl() != null) {
            hotel.setImageUrl(dto.getImageUrl());
        }

        if (dto.getActive() != null) {
            hotel.setActive(dto.getActive());
        }

        // Address update
        if (dto.getAddress() != null) {

            Address address = hotel.getAddress();

            if (address == null) {
                address = new Address();
                hotel.setAddress(address);
            }

            if (dto.getAddress().street() != null) {
                address.setStreet(dto.getAddress().street());
            }
            if (dto.getAddress().city() != null) {
                address.setCity(dto.getAddress().city());
            }
            if (dto.getAddress().state() != null) {
                address.setState(dto.getAddress().state());
            }
            if (dto.getAddress().country() != null) {
                address.setCountry(dto.getAddress().country());
            }
            if (dto.getAddress().zipCode() != null) {
                address.setZipCode(dto.getAddress().zipCode());
            }
        }

        log.info("Hotel updated successfully with id: {}", id);
        return HotelMapper.toResponseDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelById(Long id) {

        log.info("Fetching hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found with id: " + id);
                });

        return HotelMapper.toResponseDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> getAllHotels(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Hotel> hotelPage = hotelRepository.findAll(pageable);

        return hotelPage.map(HotelMapper::toResponseDTO);
    }
}
