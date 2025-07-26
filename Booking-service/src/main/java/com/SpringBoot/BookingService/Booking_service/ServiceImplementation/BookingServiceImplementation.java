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
import java.util.ArrayList;
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



        Booking booking = Booking.builder()
                .hotelId(req.hotelId())
                .roomNumber(req.roomNumber())
                .roomType(req.roomType())
                .guests(req.guests())
                .guestName(req.guests().get(0).getName())
                .checkInDate(req.checkInDate())
                .checkOutDate(req.checkOutDate())
                .status(BookingStatus.INITIATED)
                .build();


        List<Person> guestsToAssociate = new ArrayList<>();

        for (Person person : req.guests()) {
            Optional<Person> existingPerson = personRepository.findByNameAndDOBAndPincodeAndAddressAndMobileNumber(
                    person.getName(),
                    person.getDOB(),
                    person.getPincode(),
                    person.getAddress(),
                    person.getMobileNumber()
            );

            Person finalPerson = existingPerson.orElseGet(() -> personRepository.save(person));
            guestsToAssociate.add(finalPerson);
        }

// Associate the full guest list with the booking
        booking.setGuests(guestsToAssociate);

        //Booking Save Initiated
        bookingRepository.save(booking);
        try {
            //deduct rooms
            inventoryClient.deductRooms(req.hotelId(), req.roomType().toString(), 1);
            // Save Booking and marking it Reserved.
            booking.setStatus(BookingStatus.RESERVED);
            booking = bookingRepository.save(booking);
            //Get Price from Billing Service
            BigDecimal price = pricingClient.getPriceByBookingId(booking.getBookingId());

            //Setting up the price and Marking it PRICED
            booking.setPrice(price);
            booking.setStatus(BookingStatus.PRICED);
            booking = bookingRepository.save(booking);

            //Only confirm Booking can go for Payment
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } catch (Exception e) {

            try {
                if(booking.getStatus() == BookingStatus.RESERVED)
                    inventoryClient.restoreRooms(req.hotelId(), req.roomType().toString(), 1);
            } catch (Exception restoreEx) {
                // Log for manual intervention
                throw new RuntimeException("Room Restoration has been failed, Restore it manually" +
                        "Hotel Id : " + req.hotelId() + " Room Type : " + req.roomType());
            }
            // Compensation
            booking.setStatus(BookingStatus.CANCELED);
            bookingRepository.save(booking);


            throw new RuntimeException("Booking failed." + e);
        }



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
