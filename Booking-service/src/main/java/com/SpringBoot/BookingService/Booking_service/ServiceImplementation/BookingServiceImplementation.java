package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.HotelClient;
import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.DTO.*;
import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public BookingResponseDTO createBooking(BookingRequestDTO req) {
        // 1. Validate hotel & room exist
        hotelClient.getHotelById(req.hotelId());
        RoomDTO room = roomClient.getRoomByRoomNumber(req.roomNumber());
        if (!room.hotelId().equals(req.hotelId()) ||
                !room.roomType().equalsIgnoreCase(req.roomType().name()) ||
                !room.available()) {
            throw new IllegalArgumentException("Invalid or unavailable room selected.");
        }


        // 2. Check date overlap against existing bookings
        var conflicts = bookingRepository.findConflicts(
                req.hotelId(),
                req.roomType(),
                req.roomNumber(),
                req.checkInDate(),
                req.checkOutDate()
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Room already booked for given dates");
        }

        // 3. Deduct inventory
        inventoryClient.deductRooms(req.hotelId(), req.roomType().toString(), 1);

        // 4. Save booking
        Booking booking = Booking.builder()
                .hotelId(req.hotelId())
                .roomNumber(req.roomNumber())
                .roomType(req.roomType())
                .guestName(req.guestName())
                .checkInDate(req.checkInDate())
                .checkOutDate(req.checkOutDate())
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);

        // 5. Build response (you can calculate price via roomDTO.getBasePrice() if needed)
        double price = room.basePrice();
        return mapBookingToBookingResponseDTO(booking,price);

    }

    @Override
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        // Restore inventory
        inventoryClient.restoreRooms(booking.getHotelId(), booking.getRoomNumber(), 1);

        return  mapBookingToBookingResponseDTO(booking, 0.0);
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        return  mapBookingToBookingResponseDTO(booking, 0.0);

    }

    @Override
    public List<BookingResponseDTO> getBookingsByStatus(String bookingStatus) {
        BookingStatus bookingStatus1 = BookingStatus.valueOf( bookingStatus.toUpperCase());
        List<Booking> booking = bookingRepository.findByStatus(bookingStatus1);
        List<BookingResponseDTO> responseDTOS = booking.stream()
                .map(b->mapBookingToBookingResponseDTO(b,0.0))
                .toList();
        return  responseDTOS;
    }

    @Override
    public BookingResponseDTO getBookingByRoomNumber(String roomNumber) {
        Booking booking = bookingRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with Room Number: " + roomNumber));
        return mapBookingToBookingResponseDTO(booking,0.0);
    }


    //helper Methods
    public  BookingResponseDTO mapBookingToBookingResponseDTO(Booking booking,double price)
    {
        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getHotelId(),
                booking.getRoomNumber(),
                booking.getRoomType(),
                booking.getGuestName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus(),
                price // you could calculate refund or show price if needed
        );
    }
}
