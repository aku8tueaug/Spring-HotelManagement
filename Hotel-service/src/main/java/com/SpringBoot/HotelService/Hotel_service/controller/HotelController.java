package com.SpringBoot.HotelService.Hotel_service.controller;


import com.SpringBoot.HotelService.Hotel_service.DTO.HotelCreateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelResponseDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelUpdateRequestDTO;
import com.SpringBoot.HotelService.Hotel_service.Service.HotelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Validated
public class HotelController {

    private final HotelService hotelService;

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HotelResponseDTO> createHotel(
            @Valid @RequestBody HotelCreateRequestDTO request) {

        HotelResponseDTO response = hotelService.createHotel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> getHotelById(
            @PathVariable @Min(1) Long id) {

        HotelResponseDTO response = hotelService.getHotelById(id);

        return ResponseEntity.ok(response);
    }

    // UPDATE (Partial update)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> updateHotel(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody HotelUpdateRequestDTO request) {

        HotelResponseDTO response = hotelService.updateHotel(id, request);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<Page<HotelResponseDTO>> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<HotelResponseDTO> response =
                hotelService.getAllHotels(page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> deleteHotelById(@PathVariable Long id)
    {
        HotelResponseDTO responseDTO = hotelService.deleteHotelById(id);
        return ResponseEntity.ok(responseDTO);
    }
}