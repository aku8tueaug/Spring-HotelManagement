package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.DTO.Hotel;
import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import com.SpringBoot.RoomService.Room_service.Repository.RoomRepository;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final HotelClient hotelClient;

    public RoomController( RoomRepository roomRepository,HotelClient hotelClient)
    {
        this.roomRepository = roomRepository;
        this.hotelClient = hotelClient;
    }

    @GetMapping
    public List<Room> getAllRooms()
    {
        return roomRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody @Valid Room room) {
            Hotel hotel = hotelClient.getHotelById(room.getHotelId());

            Room savedRoom = roomRepository.save(room);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedRoom);
    }


    @GetMapping("/available")
    public List<Room> getAvailableRooms()
    {
        return roomRepository.findByIsAvailableTrue();
    }

    @GetMapping("/type/{roomType}")
    public List<Room> getRoomByRoomType(@PathVariable("roomType") String roomType)
    {
        RoomType roomType1 = RoomType.valueOf(roomType.toUpperCase());
        return roomRepository.findByRoomType(roomType1);
    }

    @GetMapping("hotels/{hotelId}")
    public  ResponseEntity<?> getRoomByHotelId(@PathVariable("hotelId") Long hotelId)
    {
            Hotel hotel = hotelClient.getHotelById(hotelId);
            List<Room> rooms= roomRepository.findByHotelId(hotelId);   // return a list of room List<Room>
            return ResponseEntity.ok(rooms);

    }

}


