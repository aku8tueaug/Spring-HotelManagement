package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.HotelClient;
import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingRequestDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.BookingResponseDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.HotelDTO;
import com.SpringBoot.BookingService.Booking_service.DTO.RoomDTO;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service

public class BookingServiceImplementation implements BookingService {
    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;
    private final InventoryClient inventoryClient;

    public BookingServiceImplementation(BookingRepository bookingRepository, HotelClient hotelClient, RoomClient roomClient, InventoryClient inventoryClient) {
        this.bookingRepository = bookingRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
        this.inventoryClient = inventoryClient;
    }


    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        //check hotelId and RoomId
        HotelDTO hotelDTO = hotelClient.getHotelById(request.hotelId());
        RoomDTO roomDTO = roomClient.getRoomByType(request.roomType().toString());


    }

    @Override
    public BookingResponseDTO cancelBooking(Long bookingId) {
        return null;
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        return null;
    }

    @Override
    public BookingResponseDTO getBookingByStatus(BookingStatus bookingStatus) {
        return null;
    }

    @Override
    public BookingResponseDTO getBookingWithRoomId(Long roomId) {
        return null;
    }
}
