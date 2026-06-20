package com.SpringBoot.BookingService.Booking_service.ServiceImplementation;

import com.SpringBoot.BookingService.Booking_service.Client.BillingClient;
import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.DTO.*;
import com.SpringBoot.BookingService.Booking_service.Entity.Booking;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.Guest;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import com.SpringBoot.BookingService.Booking_service.Exception.BookingCreationFailedException;
import com.SpringBoot.BookingService.Booking_service.Exception.InsufficientInventoryException;
import com.SpringBoot.BookingService.Booking_service.Repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplementationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private RoomClient roomClient;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private BookingServiceImplementation bookingService;

    // --- Helper Methods to generate test objects ---

    private BookingRequestDTO createValidRequestDTO() {
        GuestDTO guest = new GuestDTO("John Doe", "PASSPORT", "P12345", 30);
        return new BookingRequestDTO(
                1L,
                RoomType.STANDARD,
                100L,
                1,
                List.of(guest),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );
    }

    private Booking createBookingEntity(Long bookingId, BookingStatus status) {
        Guest guest = new Guest("John Doe", "PASSPORT", "P12345", 30);
        return Booking.builder()
                .bookingId(bookingId)
                .hotelId(1L)
                .roomType(RoomType.STANDARD)
                .userId(100L)
                .roomCount(1)
                .guests(new ArrayList<>(List.of(guest)))
                .plannedCheckInDateTime(LocalDateTime.now().plusDays(1))
                .plannedCheckOutDateTime(LocalDateTime.now().plusDays(3))
                .status(status)
                .bookingDate(LocalDate.now())
                .totalAmount(new BigDecimal("100.00"))
                .taxAmount(new BigDecimal("10.00"))
                .finalAmount(new BigDecimal("110.00"))
                .build();
    }

    // ==========================================
    // 1. createBooking() Tests
    // ==========================================

    @Test
    void testCreateBooking_Success() throws BookingCreationFailedException {
        BookingRequestDTO request = createValidRequestDTO();
        PriceResponseDTO priceResponse = new PriceResponseDTO(
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("110.00")
        );

        Booking initialBooking = Booking.builder()
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .userId(request.userId())
                .roomCount(request.roomCount())
                .guests(List.of(new Guest("John Doe", "PASSPORT", "P12345", 30)))
                .plannedCheckInDateTime(request.checkInDateTime())
                .plannedCheckOutDateTime(request.checkOutDateTime())
                .status(BookingStatus.INITIATED)
                .build();

        Booking savedInitiated = Booking.builder()
                .bookingId(456L)
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .userId(request.userId())
                .roomCount(request.roomCount())
                .guests(List.of(new Guest("John Doe", "PASSPORT", "P12345", 30)))
                .plannedCheckInDateTime(request.checkInDateTime())
                .plannedCheckOutDateTime(request.checkOutDateTime())
                .status(BookingStatus.INITIATED)
                .totalAmount(priceResponse.subtotal())
                .taxAmount(priceResponse.taxAmount())
                .finalAmount(priceResponse.finalAmount())
                .build();

        Booking savedConfirmed = Booking.builder()
                .bookingId(456L)
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .userId(request.userId())
                .roomCount(request.roomCount())
                .guests(List.of(new Guest("John Doe", "PASSPORT", "P12345", 30)))
                .plannedCheckInDateTime(request.checkInDateTime())
                .plannedCheckOutDateTime(request.checkOutDateTime())
                .status(BookingStatus.CONFIRMED)
                .totalAmount(priceResponse.subtotal())
                .taxAmount(priceResponse.taxAmount())
                .finalAmount(priceResponse.finalAmount())
                .build();

        when(inventoryClient.checkAvailability(any())).thenReturn(true);
        when(billingClient.calculatePrice(any())).thenReturn(priceResponse);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedInitiated)
                .thenReturn(savedConfirmed);

        BookingResponseDTO response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(456L, response.bookingId());
        assertEquals(BookingStatus.CONFIRMED, response.status());
        assertEquals(new BigDecimal("110.00"), response.finalAmount());

        verify(inventoryClient).checkAvailability(any());
        verify(billingClient).calculatePrice(any());
        verify(inventoryClient).reserveInventory(any());
        verify(billingClient).createInvoice(any());
        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_CheckOutNotAfterCheckIn_ThrowsIllegalArgumentException() {
        GuestDTO guest = new GuestDTO("John Doe", "PASSPORT", "P12345", 30);
        LocalDateTime now = LocalDateTime.now();
        // Check-out date is equal to check-in date
        BookingRequestDTO request = new BookingRequestDTO(
                1L, RoomType.STANDARD, 100L, 1, List.of(guest), now, now
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Check-out date must be at least 1 day after check-in date", exception.getMessage());
        verifyNoInteractions(inventoryClient, billingClient, bookingRepository);
    }

    @Test
    void testCreateBooking_GuestCountExceedsCapacity_ThrowsIllegalArgumentException() {
        List<GuestDTO> fiveGuests = Collections.nCopies(5, new GuestDTO("Guest", "ID", "123", 25));
        BookingRequestDTO request = new BookingRequestDTO(
                1L, RoomType.STANDARD, 100L, 1, fiveGuests,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Guest count exceeds room capacity", exception.getMessage());
        verifyNoInteractions(inventoryClient, billingClient, bookingRepository);
    }

    @Test
    void testCreateBooking_InsufficientInventory_ThrowsInsufficientInventoryException() {
        BookingRequestDTO request = createValidRequestDTO();
        when(inventoryClient.checkAvailability(any())).thenReturn(false);

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Requested Room is not available", exception.getMessage());
        verify(inventoryClient).checkAvailability(any());
        verifyNoMoreInteractions(inventoryClient);
        verifyNoInteractions(billingClient, bookingRepository);
    }

    @Test
    void testCreateBooking_InventoryReservationFails_RollbacksToCanceled() {
        BookingRequestDTO request = createValidRequestDTO();
        PriceResponseDTO priceResponse = new PriceResponseDTO(
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("110.00")
        );

        Booking savedInitiated = Booking.builder()
                .bookingId(456L)
                .hotelId(request.hotelId())
                .roomType(request.roomType())
                .userId(request.userId())
                .roomCount(request.roomCount())
                .guests(List.of(new Guest("John Doe", "PASSPORT", "P12345", 30)))
                .plannedCheckInDateTime(request.checkInDateTime())
                .plannedCheckOutDateTime(request.checkOutDateTime())
                .status(BookingStatus.INITIATED)
                .totalAmount(priceResponse.subtotal())
                .taxAmount(priceResponse.taxAmount())
                .finalAmount(priceResponse.finalAmount())
                .build();

        when(inventoryClient.checkAvailability(any())).thenReturn(true);
        when(billingClient.calculatePrice(any())).thenReturn(priceResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedInitiated);

        // Simulate reservation failure
        doThrow(new RuntimeException("Inventory service down")).when(inventoryClient).reserveInventory(any());

        BookingCreationFailedException exception = assertThrows(BookingCreationFailedException.class, () -> {
            bookingService.createBooking(request);
        });

        assertTrue(exception.getMessage().contains("Booking Creation Failed"));
        
        // Verify that it updated the booking state to CANCELED and saved it
        verify(bookingRepository).save(argThat(booking -> booking.getStatus() == BookingStatus.CANCELED));
    }

    // ==========================================
    // 2. cancelBooking() Tests
    // ==========================================

    @Test
    void testCancelBooking_Success() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.cancelBooking(bookingId);

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELED, response.status());
        verify(inventoryClient).releaseInventory(any());
        verify(billingClient).cancelInvoice(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_BookingNotFound_ThrowsIllegalArgumentException() {
        Long bookingId = 123L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });

        assertEquals("Booking not found", exception.getMessage());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(inventoryClient, billingClient);
    }

    @Test
    void testCancelBooking_InvalidStatus_ThrowsIllegalStateException() {
        Long bookingId = 123L;
        Booking alreadyCanceled = createBookingEntity(bookingId, BookingStatus.CANCELED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(alreadyCanceled));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });

        assertEquals("Only confirmed booking can be cancelled", exception.getMessage());
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(inventoryClient, billingClient);
    }

    @Test
    void testCancelBooking_BillingCancelFails_CompletesCancellation() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Simulate billing cancel throwing an exception
        doThrow(new RuntimeException("Billing Service unreachable")).when(billingClient).cancelInvoice(bookingId);

        BookingResponseDTO response = bookingService.cancelBooking(bookingId);

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELED, response.status());
        // Verify releaseInventory was called and booking was saved as CANCELED despite billing service error
        verify(inventoryClient).releaseInventory(any());
        verify(bookingRepository).save(any(Booking.class));
    }

    // ==========================================
    // 3. checkIn() Tests
    // ==========================================

    @Test
    void testCheckIn_Success() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);
        confirmedBooking.setPlannedCheckInDateTime(LocalDateTime.now().minusHours(1)); // in the past/today

        List<RoomDTO> rooms = List.of(
                new RoomDTO(1L, RoomType.STANDARD, "101"),
                new RoomDTO(1L, RoomType.STANDARD, "102")
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));
        when(roomClient.getRoomsByHotelAndType(1L, "STANDARD")).thenReturn(rooms);
        when(bookingRepository.findOccupiedRooms(1L, RoomType.STANDARD)).thenReturn(List.of("101")); // 101 occupied, 102 free
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.checkIn(bookingId);

        assertNotNull(response);
        assertEquals(BookingStatus.CHECKED_IN, response.status());
        assertEquals(List.of("102"), response.guests().size() > 0 ? confirmedBooking.getRoomNumber() : null);
        assertNotNull(confirmedBooking.getActualCheckInDateTime());
        verify(bookingRepository).save(confirmedBooking);
    }

    @Test
    void testCheckIn_CheckInDateNotReached_ThrowsIllegalStateException() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);
        confirmedBooking.setPlannedCheckInDateTime(LocalDateTime.now().plusDays(2)); // in the future

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.checkIn(bookingId);
        });

        assertEquals("Check-in date not reached", exception.getMessage());
        verifyNoInteractions(roomClient);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCheckIn_InvalidBookingStatus_ThrowsIllegalStateException() {
        Long bookingId = 123L;
        Booking initiatedBooking = createBookingEntity(bookingId, BookingStatus.INITIATED);
        initiatedBooking.setPlannedCheckInDateTime(LocalDateTime.now().minusHours(1));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(initiatedBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.checkIn(bookingId);
        });

        assertTrue(exception.getMessage().contains("Check-in can not be performed on INITIATED"));
        verifyNoInteractions(roomClient);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCheckIn_NotEnoughRoomsAvailable_ThrowsIllegalStateException() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);
        confirmedBooking.setPlannedCheckInDateTime(LocalDateTime.now().minusHours(1));

        List<RoomDTO> rooms = List.of(
                new RoomDTO(1L, RoomType.STANDARD, "101")
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));
        when(roomClient.getRoomsByHotelAndType(1L, "STANDARD")).thenReturn(rooms);
        when(bookingRepository.findOccupiedRooms(1L, RoomType.STANDARD)).thenReturn(List.of("101")); // all rooms occupied

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.checkIn(bookingId);
        });

        assertEquals("Not enough rooms available for check-in", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    // ==========================================
    // 4. checkOut() Tests
    // ==========================================

    @Test
    void testCheckOut_Success() {
        Long bookingId = 123L;
        Booking checkedInBooking = createBookingEntity(bookingId, BookingStatus.CHECKED_IN);
        checkedInBooking.setRoomNumber(List.of("102"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(checkedInBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.checkOut(bookingId);

        assertNotNull(response);
        assertEquals(BookingStatus.CHECKED_OUT, response.status());
        assertNotNull(checkedInBooking.getActualCheckOutDateTime());
        verify(inventoryClient).releaseInventory(any());
        verify(bookingRepository).save(checkedInBooking);
    }

    @Test
    void testCheckOut_InvalidBookingStatus_ThrowsIllegalStateException() {
        Long bookingId = 123L;
        Booking confirmedBooking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.checkOut(bookingId);
        });

        assertTrue(exception.getMessage().contains("Check-out can not be performed on CONFIRMED"));
        verifyNoInteractions(inventoryClient);
        verify(bookingRepository, never()).save(any());
    }

    // ==========================================
    // 5. Query / Simple Retrieval Tests
    // ==========================================

    @Test
    void testGetBooking_Success() {
        Long bookingId = 123L;
        Booking booking = createBookingEntity(bookingId, BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDTO response = bookingService.getBooking(bookingId);

        assertNotNull(response);
        assertEquals(bookingId, response.bookingId());
    }

    @Test
    void testGetBookingsByStatus() {
        Booking booking = createBookingEntity(123L, BookingStatus.CONFIRMED);
        when(bookingRepository.findByStatus(BookingStatus.CONFIRMED)).thenReturn(List.of(booking));

        List<BookingResponseDTO> list = bookingService.getBookingsByStatus(BookingStatus.CONFIRMED);

        assertEquals(1, list.size());
        assertEquals(BookingStatus.CONFIRMED, list.get(0).status());
    }

    @Test
    void testGetBookingsByUserId() {
        Booking booking = createBookingEntity(123L, BookingStatus.CONFIRMED);
        when(bookingRepository.findByUserId(100L)).thenReturn(List.of(booking));

        List<BookingResponseDTO> list = bookingService.getBookingsByUserId(100L);

        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).userId());
    }

    @Test
    void testGetBookingsByHotelId() {
        Booking booking = createBookingEntity(123L, BookingStatus.CONFIRMED);
        when(bookingRepository.findByHotelId(1L)).thenReturn(List.of(booking));

        List<BookingResponseDTO> list = bookingService.getBookingsByHotelId(1L);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).hotelId());
    }
}
