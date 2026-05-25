package com.SpringBoot.RoomService.Room_service.HTTPClient;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface HotelClient {
    @GetExchange("/internal/hotels/{id}/exists")
    public void validateHotelExists(@PathVariable Long id) ;

}
