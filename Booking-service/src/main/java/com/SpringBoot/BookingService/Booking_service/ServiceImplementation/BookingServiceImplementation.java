package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.HotelClient;
import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.PricingClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.DTO.*;
import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.Person;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import com.SpringBoot.BookingService.Booking_service.Repository.PersonRepository;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service

public class BookingServiceImplementation implements BookingService {
    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;
    private final InventoryClient inventoryClient;
    private final PersonRepository personRepository;
    private final PricingClient pricingClient;

    public BookingServiceImplementation(BookingRepository bookingRepository,
                                        HotelClient hotelClient,
                                        RoomClient roomClient,
                                        InventoryClient inventoryClient,
                                        PersonRepository personRepository,
                                        PricingClient pricingClient
    ) {
        this.bookingRepository = bookingRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
        this.inventoryClient = inventoryClient;
        this.personRepository = personRepository;
        this.pricingClient = pricingClient;
    }


    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO req) {
        // 1. Validate hotel & room exist
        hotelClient.getHotelById(req.hotelId());
        RoomDTO room = roomClient.getRoomByRoomNumber(req.roomNumber());

        if (room == null || !room.hotelId().equals(req.hotelId()) ||
                !room.roomType().equalsIgnoreCase(req.roomType().name()) ||
                !room.available() ) {
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
                .guests(req.guests())
                .guestName(req.guests().get(0).getName())
                .checkInDate(req.checkInDate())
                .checkOutDate(req.checkOutDate())
                .status(BookingStatus.CONFIRMED)
                .build();

        for(Person person : req.guests())
        {
//            person.setMobileNumber(
//                    person.getCountryCode()+
//                    person.getMobileNumber());

            Optional<Person> person1 =  personRepository.findByNameAndDOBAndPincodeAndAddressAndMobileNumber(person.getName(),
                    person.getDOB(),
                    person.getPincode(),
                    person.getAddress(),
                    person.getMobileNumber());

            if(person1.isEmpty())
            {
                personRepository.save(person);
            }

        }

        booking = bookingRepository.save(booking);

        // 5. Build response (you can calculate price via roomDTO.getBasePrice() if needed)
        BigDecimal price = pricingClient.getPriceByBookingId(booking.getBookingId());

        booking.setPrice(price);
        bookingRepository.save(booking);
        return mapBookingToBookingResponseDTO(booking);

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

        return  mapBookingToBookingResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        return  mapBookingToBookingResponseDTO(booking);

    }

    @Override
    public List<BookingResponseDTO> getBookingsByStatus(String bookingStatus) {
        BookingStatus bookingStatus1 = BookingStatus.valueOf( bookingStatus.toUpperCase());
        List<Booking> booking = bookingRepository.findByStatus(bookingStatus1);

        List<BookingResponseDTO> responseDTOS = booking.stream()
                .map(b->mapBookingToBookingResponseDTO(b))
                .toList();
        return  responseDTOS;
    }

    @Override
    public BookingResponseDTO getBookingByRoomNumber(String roomNumber) {
        Booking booking = bookingRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with Room Number: " + roomNumber));
        return mapBookingToBookingResponseDTO(booking);
    }

    @Override
    public BookingResponseDTO addAdditionalCharges(Long bookingId, BigDecimal extraCharges)
    {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if(booking.getPrice() == null)
        {
            BigDecimal price = pricingClient.getPriceByBookingId(bookingId);
            booking.setPrice(price);
        }
        booking.setPrice(booking.getPrice().add(extraCharges));
        booking = bookingRepository.save(booking);
        return mapBookingToBookingResponseDTO(booking);
    }


    //helper Methods
    public  BookingResponseDTO mapBookingToBookingResponseDTO(Booking booking)
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
                booking.getPrice(),
                booking.getGuests()
        );
    }
}
