package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/internal/rooms")
@RequiredArgsConstructor
public class InternalRoomController {

    private final RoomService roomService;
    @PatchMapping("/hotels/{hotelId}/deactivate")
    public ResponseEntity<Void> deactivateRooms(
            @PathVariable Long hotelId) {
        roomService.deactivateRoomsByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/hotels/{hotelId}/reactivate")
    public ResponseEntity<Void> reactivateRooms(
            @PathVariable Long hotelId) {
        roomService.reactivateRoomsByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
}
