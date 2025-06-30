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
      public AvailabilityResponseDTO getAvailableRoomByHotelId(Long hotelId);
      public  AvailabilityResponseDTO getAvailableRoomsByRoomTypes(String roomType);
      public AvailabilityResponseDTO getAvailableRoomsByHotelIdAndRoomType(Long hotelId, String roomType);
}

//@Service
//@RequiredArgsConstructor
//public class AvailabilityService {

//    private final InventoryClient inventoryClient;
//    private final BookingRepository bookingRepository;

    /**
     * Returns available room counts per type for a hotel
     * between from (inclusive) and to (exclusive).
     */
//    public Map<RoomType, Integer> findAvailableByType(Long hotelId, LocalDate from, LocalDate to) {
//        // 1. Static totals
//        List<InventoryDTO> inventories = inventoryClient.getByHotelId(hotelId);
//        Map<RoomType, Integer> totals = inventories.stream()
////                .collect(Collectors.toMap(InventoryDTO::roomType, InventoryDTO::totalRooms));
//                .collect(Collectors.toMap(InventoryDTO::roomType, InventoryDTO::totalRooms));
//        // 2. Subtract booked counts
//        for (InventoryDTO inv : inventories) {
//            long bookedCount = bookingRepository.findConflicts(inv.roomType(), from, to).size();
//            totals.compute(inv.roomType(),
//                    (rt, tot) -> Math.max(0, tot - (int)bookedCount));
//        }
//        return totals;
//    }
//}
