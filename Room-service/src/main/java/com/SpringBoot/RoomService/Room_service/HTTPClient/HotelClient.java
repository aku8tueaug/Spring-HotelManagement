package com.SpringBoot.RoomService.Room_service.HTTPClient;


import com.SpringBoot.RoomService.Room_service.DTO.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface HotelClient {
    @GetExchange("hotels/{id}")
    public Hotel getHotelById(@PathVariable Long id) ;

}
