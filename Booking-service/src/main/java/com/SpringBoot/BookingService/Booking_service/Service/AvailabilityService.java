package com.SpringBoot.BookingService.Booking_service.Service;
import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.DTO.AvailabilityResponseDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.InventoryDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface AvailabilityService{
      public List<AvailabilityResponseDTO> getAvailableRoomByHotelId(Long hotelId);//It should return list of rooms with hotelID like map
      public  List<AvailabilityResponseDTO> getAvailableRoomsByRoomTypes(String roomType);//It should return List of count of each room type with hotel id
      public AvailabilityResponseDTO getAvailableRoomsByHotelIdAndRoomType(Long hotelId, String roomType); // it should return only available room
      public AvailabilityResponseDTO checkRoomAvailabilityBetweenDates(Long hotelId,String roomTypeStr,LocalDate checkIn,LocalDate checkOut);
}
