package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.DTO.CreateMultipleRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.Hotel;
import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import com.SpringBoot.RoomService.Room_service.Repository.RoomRepository;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController( RoomService roomService)
    {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> Rooms()
    {

        return roomService.getAllRooms();
    }

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody @Valid Room room) {

            Room savedRoom = roomService.addRoom(room);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedRoom);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> addMultipleRoom(@RequestBody @Valid CreateMultipleRoomRequestDTO multipleRoomRequestDTO) {

        List<Room> savedRoom =  roomService.addMultipleRoom(multipleRoomRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRoom);
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms()
    {
        return roomService.getAvailableRooms();
    }

    @GetMapping("/type/{roomType}")
    public List<Room> getRoomByRoomType(@PathVariable("roomType") String roomType)
    {
       return roomService.getRoomByRoomType(roomType);
    }

    @GetMapping("hotels/{hotelId}")
    public  ResponseEntity<?> getRoomByHotelId(@PathVariable("hotelId") Long hotelId)
    {

            List<Room> rooms= roomService.getRoomByHotelId(hotelId); // return a list of room List<Room>
            return ResponseEntity.ok(rooms);

    }
    @GetMapping("/{roomNumber}")
    public ResponseEntity<?> getRoomByRoomNumber(@PathVariable("roomNumber") String roomNumber)
    {
        return ResponseEntity.ok(roomService.getRoomByRoomNumber(roomNumber));
    }


}


