package com.SpringBoot.HotelService.Hotel_service.HTTPClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface RoomClient {
    @PatchExchange("/internal/rooms/hotels/{hotelId}/deactivate")
    void deactivateRoomsByHotelId(@PathVariable Long hotelId);
}
