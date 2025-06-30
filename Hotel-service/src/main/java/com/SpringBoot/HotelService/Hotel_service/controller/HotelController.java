package com.SpringBoot.HotelService.Hotel_service.controller;



import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import com.SpringBoot.HotelService.Hotel_service.Repository.HotelRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository)
    {
        this.hotelRepository = hotelRepository;
    }

    @PostMapping
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelRepository.save(hotel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable Long id) {
        try {
            Hotel hotel = hotelRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Hotel not found with id: " + id));
            return ResponseEntity.ok(hotel);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new NoSuchElementException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
        return ResponseEntity.ok("Hotel deleted with id: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel updated) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hotel not found with id: " + id));

        hotel.setName(updated.getName());
        hotel.setAddress(updated.getAddress());
        hotel.setCity(updated.getCity());
        hotel.setContactNumber(updated.getContactNumber());

        return ResponseEntity.ok(hotelRepository.save(hotel));
    }
}

