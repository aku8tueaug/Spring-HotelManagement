package com.SpringBoot.BookingService.Booking_service.Client;

import com.SpringBoot.BookingService.Booking_service.DTO.InventoryDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
public interface InventoryClient {
    @GetExchange("/hotel/{hotelId}")
    public List<InventoryDTO> getByHotelId(@PathVariable("hotelId") Long hotelId);

    @GetExchange("/inventory/")
    public List<InventoryDTO> getByHotelIdAndRoomType(Long hotelId, String roomType);
}
