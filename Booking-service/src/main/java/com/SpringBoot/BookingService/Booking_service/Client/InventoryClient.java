package com.SpringBoot.BookingService.Booking_service.Client;

import com.SpringBoot.BookingService.Booking_service.DTO.InventoryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange
public interface InventoryClient {
    @GetExchange("/inventory/hotels/{hotelId}")
    public List<InventoryDTO> getInventoryByHotelId(@PathVariable("hotelId") Long hotelId);

    @GetExchange("/inventory/roomType/{roomType}")
    public List<InventoryDTO> getInventoriesByRoomType(@PathVariable("roomType") String roomType);

    @GetExchange("/inventory/hotelsAndRoomType")
    public InventoryDTO getByHotelIdAndRoomType(
            @RequestParam("hotelId") Long hotelId,
            @RequestParam("roomType") String roomType
    );
    @PostExchange("/inventory/deduct")
    public String deductRooms(
            @RequestParam Long hotelId,
            @RequestParam String roomType,
            @RequestParam int count);
    @PostExchange("/inventory/restore")
    public String restoreRooms(
            @RequestParam Long hotelId,
            @RequestParam String roomType,
            @RequestParam int count);
}
