package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.DTO.CreateMultipleRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.CreateRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.DTO.ResponseRoomDTO;
import com.SpringBoot.RoomService.Room_service.DTO.UpdateRoomRequestDTO;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ResponseRoomDTO>> Rooms()
    {

        List<ResponseRoomDTO> responseRoomDTOList = roomService.getAllRooms();

        return ResponseEntity.ok(responseRoomDTOList);
    }

    @PostMapping
    public ResponseEntity<ResponseRoomDTO> addRoom(@RequestBody @Valid CreateRoomRequestDTO room) {

            ResponseRoomDTO responseRoomDTO = roomService.addRoom(room);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseRoomDTO);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResponseRoomDTO>> addMultipleRoom(@RequestBody @Valid CreateMultipleRoomRequestDTO multipleRoomRequestDTO) {

        List<ResponseRoomDTO> savedRoom =  roomService.addMultipleRoom(multipleRoomRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRoom);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ResponseRoomDTO>> getActiveRooms()
    {
        List<ResponseRoomDTO> responseRoomDTOList = roomService.getActiveRooms();

        return ResponseEntity.ok(responseRoomDTOList);
    }

    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<ResponseRoomDTO>> getRoomByRoomType(@PathVariable("roomType") RoomType roomType)
    {
        List<ResponseRoomDTO> responseRoomDTOList = roomService.getRoomByRoomType(roomType);

        return ResponseEntity.ok(responseRoomDTOList);
    }

    @GetMapping("/hotels/{hotelId}")
    public  ResponseEntity<List<ResponseRoomDTO>> getRoomByHotelId(@PathVariable("hotelId") Long hotelId)
    {

        List<ResponseRoomDTO> responseRoomDTOList = roomService.getRoomByHotelId(hotelId); // return a list of room List<Room>
            return ResponseEntity.ok(responseRoomDTOList);

    }
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<ResponseRoomDTO> getRoomByRoomNumber(@PathVariable("roomNumber") String roomNumber)
    {
        return ResponseEntity.ok(roomService.getRoomByRoomNumber(roomNumber));
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<ResponseRoomDTO> updateRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateRoomRequestDTO request
    ) {

        ResponseRoomDTO response =
                roomService.updateRoom(roomId, request);

        return ResponseEntity.ok(response);
    }


}


