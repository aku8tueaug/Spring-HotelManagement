package com.SpringBoot.BookingService.Booking_service.Client;


import com.SpringBoot.BookingService.Booking_service.DTO.HotelDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface HotelClient {

    @GetExchange("/hotels/{id}")
    public HotelDTO getHotelById(@PathVariable("id") Long id);

}
