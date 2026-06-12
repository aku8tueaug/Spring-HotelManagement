package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.DTO.*;
import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.Guest;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImplementation implements BookingService {

    private final BookingRepository bookingRepository;
    private final InventoryClient inventoryClient;
    private final RoomClient roomClient;

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
            isCheckOutDateAfterCheckInDate(request);

            InventoryAvailabilityRequestDTO availabilityRequestDTO =
                    bookReqDTO_To_AvailReqDTO(request);

            validateGuestCount(request);

            boolean available = inventoryClient.checkAvailability(availabilityRequestDTO);

            if(!available)
            {
                throw  new IllegalStateException("Requested Room is not available");
            }

            Booking booking = bookReqDTO_To_BookingEntity(request);
            //Saving Booking Initiated state
            booking = bookingRepository.save(booking);
            try
            {
                InventoryReservationRequestDTO reservationRequestDTO =
                        bookReqDTO_To_InventReservationReqDTO(request);
                inventoryClient.reserveInventory(reservationRequestDTO);
                booking.setStatus(BookingStatus.CONFIRMED);
                booking = bookingRepository.save(booking);
            }
            catch (Exception e)
            {
                booking.setStatus(BookingStatus.CANCELED);
                bookingRepository.save(booking);
                throw new RuntimeException(" Booking Creation Failed ", e);
            }

            return bookingEntity_To_BookingRespDTO(booking);
    }

    @Override
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(()-> new IllegalArgumentException("Booking not found"));

        if(booking.getStatus() != BookingStatus.CONFIRMED)
        {
            throw new IllegalStateException("Only confirmed booking can be cancelled");
        }

        InventoryReservationRequestDTO reservationRequestDTO =
                booking_To_InventReservationReqDTO(booking);

        inventoryClient.releaseInventory(reservationRequestDTO);

        booking.setStatus(BookingStatus.CANCELED);
        booking = bookingRepository.save(booking);
        return bookingEntity_To_BookingRespDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBooking(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException("Booking not found" ));

        return bookingEntity_To_BookingRespDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status)
                .stream()
                .map(this::bookingEntity_To_BookingRespDTO)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByUserId(Long userId)
    {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::bookingEntity_To_BookingRespDTO)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByHotelId(Long hotelId)
    {
        return bookingRepository.findByHotelId(hotelId)
                .stream()
                .map(this::bookingEntity_To_BookingRespDTO)
                .toList();
    }

    @Override
    public BookingResponseDTO checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("No booking found for this id"));


        //check-in date validation
        LocalDate today = LocalDate.now();

        if (booking.getPlannedCheckInDateTime()
                .toLocalDate()
                .isAfter(today)) {
            throw new IllegalStateException(
                    "Check-in date not reached"
            );
        }

        if(booking.getStatus() != (BookingStatus.CONFIRMED ))
        {
            throw new IllegalStateException("Check-in can not be performed on " + booking.getStatus() );
        }

        List<RoomDTO> rooms =
                roomClient.getRoomsByHotelAndType(
                        booking.getHotelId(),
                        booking.getRoomType().name()
                );

        List<String> occupied =
                bookingRepository.findOccupiedRooms(
                        booking.getHotelId(),
                        booking.getRoomType()
                );

        List<String> availableRooms =
                rooms.stream()
                        .map(RoomDTO::roomNumber)
                        .filter(r -> !occupied.contains(r))
                        .toList();

        if (availableRooms.size() < booking.getRoomCount()) {
            throw new IllegalStateException(
                    "Not enough rooms available for check-in"
            );
        }
        List<String> assignedRooms =
                availableRooms.stream()
                        .limit(booking.getRoomCount())
                        .toList();

        booking.setRoomNumber(assignedRooms);
        booking.setActualCheckInDateTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);

        bookingRepository.save(booking);
        return bookingEntity_To_BookingRespDTO(booking);
    }

    @Override
    public BookingResponseDTO checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("No booking found for this id"));

        if(booking.getStatus() != (BookingStatus.CHECKED_IN ))
        {
            throw new IllegalStateException("Check-out can not be performed on " + booking.getStatus() );
        }
        booking.setActualCheckOutDateTime(
                LocalDateTime.now()
        );

        InventoryReservationRequestDTO reservationRequestDTO =
                booking_To_InventReservationReqDTO(booking);

        inventoryClient.releaseInventory(reservationRequestDTO);

        booking.setStatus(
                BookingStatus.CHECKED_OUT
        );
        bookingRepository.save(booking);
        return bookingEntity_To_BookingRespDTO(booking);

    }


    //Helper Methods

    private boolean isCheckOutDateAfterCheckInDate(BookingRequestDTO requestDTO)
    {
        if(requestDTO.checkOutDateTime().isBefore(requestDTO.checkInDateTime()))
        {
            throw new IllegalArgumentException(
                    "Check-out date cannot be before check-in date");
        }
        return  true;
    }

    private InventoryAvailabilityRequestDTO bookReqDTO_To_AvailReqDTO(BookingRequestDTO requestDTO)
    {
        return new InventoryAvailabilityRequestDTO(
                requestDTO.hotelId(),
                requestDTO.roomType(),
                requestDTO.checkInDateTime().toLocalDate(),
                requestDTO.checkOutDateTime().toLocalDate(),
                requestDTO.roomCount()
        );
    }

    private Booking bookReqDTO_To_BookingEntity(BookingRequestDTO requestDTO)
    {
        return  Booking.builder()
                .hotelId(requestDTO.hotelId())
                .roomType(requestDTO.roomType())
                .userId(requestDTO.userId())
                .roomCount(requestDTO.roomCount())
                .guests(guestDTO_To_GuestEntity(requestDTO.guests()))
                .plannedCheckInDateTime(requestDTO.checkInDateTime())
                .plannedCheckOutDateTime(requestDTO.checkOutDateTime())
                .status(BookingStatus.INITIATED)
                .build();
    }

    private InventoryReservationRequestDTO bookReqDTO_To_InventReservationReqDTO(BookingRequestDTO requestDTO)
    {
        return new InventoryReservationRequestDTO(
                requestDTO.hotelId(),
                requestDTO.roomType(),
                requestDTO.checkInDateTime().toLocalDate(),
                requestDTO.checkOutDateTime().toLocalDate(),
                requestDTO.roomCount()
        );
    }

    private InventoryReservationRequestDTO booking_To_InventReservationReqDTO(Booking booking)
    {
        return new InventoryReservationRequestDTO(
                booking.getHotelId(),
                booking.getRoomType(),
                booking.getPlannedCheckInDateTime().toLocalDate(),
                booking.getPlannedCheckOutDateTime().toLocalDate(),
                booking.getRoomCount()
        );
    }

    private BookingResponseDTO bookingEntity_To_BookingRespDTO( Booking booking)
    {


        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getHotelId(),
                booking.getRoomType(),
                booking.getUserId(),
                booking.getRoomCount(),
                guestEntity_To_GuestDTO(booking.getGuests()),
                booking.getBookingDate(),
                booking.getPlannedCheckInDateTime(),
                booking.getPlannedCheckOutDateTime(),
                booking.getStatus()
        );
    }

    private List<GuestDTO> guestEntity_To_GuestDTO(List<Guest> guests)
    {
         return guests.stream()
                .map(this::guestEntity_To_GuestDTO )
                .toList();
    }

    private GuestDTO guestEntity_To_GuestDTO(Guest guest)
    {
        return new GuestDTO(
                guest.getFullName(),
                guest.getIdType(),
                guest.getIdNumber(),
                guest.getAge()
        );
    }

    private List<Guest> guestDTO_To_GuestEntity(List<GuestDTO> guests)
    {
        return guests.stream()
                .map(this::guestDTO_To_GuestEntity)
                .toList();
    }

    private Guest guestDTO_To_GuestEntity(GuestDTO guest)
    {
        Guest entity = new Guest();

        entity.setFullName(guest.fullName());
        entity.setIdType(guest.idType());
        entity.setIdNumber(guest.idNumber());
        entity.setAge(guest.age());

        return entity;
    }


    private void validateGuestCount(BookingRequestDTO request)
    {
        if(request.guests().size() > request.roomCount() * 4)
        {
            throw new IllegalArgumentException(
                    "Guest count exceeds room capacity"
            );
        }
    }
}
