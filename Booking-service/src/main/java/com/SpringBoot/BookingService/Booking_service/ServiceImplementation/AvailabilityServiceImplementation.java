package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.DTO.AvailabilityResponseDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.InventoryDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import com.SpringBoot.BookingService.Booking_service.Service.AvailabilityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImplementation implements AvailabilityService {

    public final InventoryClient inventoryClient;
    public final BookingRepository bookingRepository;
    public AvailabilityServiceImplementation(InventoryClient inventoryClient,BookingRepository bookingRepository )
    {
        this.inventoryClient = inventoryClient;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<AvailabilityResponseDTO> getAvailableRoomByHotelId(Long hotelId) {
        List<InventoryDTO> inventoryList = inventoryClient.getInventoryByHotelId(hotelId);

        return inventoryList.stream()
                .map(this::mapInventoryDTO_TO_AvailabilityResponseDTO)
                .toList();
    }

    @Override
    public List<AvailabilityResponseDTO> getAvailableRoomsByRoomTypes(String roomTypeStr) {
        RoomType roomType = RoomType.valueOf(roomTypeStr.toUpperCase());
        List<InventoryDTO> inventoryList = inventoryClient.getInventoriesByRoomType(roomTypeStr);

        return inventoryList.stream()
                .map(this::mapInventoryDTO_TO_AvailabilityResponseDTO)
                .toList();
    }

    @Override
    public AvailabilityResponseDTO getAvailableRoomsByHotelIdAndRoomType(Long hotelId, String roomTypeStr) {
        RoomType roomType = RoomType.valueOf(roomTypeStr.toUpperCase());
        InventoryDTO inventory = inventoryClient.getByHotelIdAndRoomType(hotelId, roomTypeStr);

        return mapInventoryDTO_TO_AvailabilityResponseDTO(inventory);
    }

    public AvailabilityResponseDTO checkRoomAvailabilityBetweenDates(
            Long hotelId,
            String roomTypeStr,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        RoomType roomType = RoomType.valueOf(roomTypeStr.toUpperCase());

        // Step 1: Get total rooms from inventory
        InventoryDTO inventory = inventoryClient.getByHotelIdAndRoomType(hotelId, roomTypeStr);
        int totalRooms = inventory.availableRooms(); // or inventory.totalRooms()

        // Step 2: Get number of conflicting bookings
        List<Booking> conflicts = bookingRepository.findBookingsInDateRange(
                hotelId, roomType, checkIn, checkOut
        );

        // Step 3: Subtract to get available count
        int availableCount = totalRooms - conflicts.size();

        return new AvailabilityResponseDTO(hotelId, roomType, Math.max(availableCount, 0));
    }


    //helper Method
    public AvailabilityResponseDTO mapInventoryDTO_TO_AvailabilityResponseDTO(InventoryDTO inventoryDTO) {
        return new AvailabilityResponseDTO(
                inventoryDTO.hotelId(),
                inventoryDTO.roomType(),
                inventoryDTO.availableRooms()
        );
    }


}
