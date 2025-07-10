package com.SpringBoot.BookingService.Booking_service.Client;

import com.SpringBoot.BookingService.Booking_service.DTO.RoomDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
public interface RoomClient {

    @GetExchange("/rooms/{roomNumber}")
    public RoomDTO getRoomByRoomNumber(@PathVariable("roomNumber") String roomNumber);

    @GetExchange("/rooms/type/{roomType}")
    public List<RoomDTO> getRoomByType(@PathVariable("roomType") String roomType);
}
