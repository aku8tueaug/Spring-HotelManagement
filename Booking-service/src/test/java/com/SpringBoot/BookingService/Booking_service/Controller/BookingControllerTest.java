package com.SpringBoot.BookingService.Booking_service.Controller;

import com.SpringBoot.BookingService.Booking_service.DTO.*;
import com.SpringBoot.BookingService.Booking_service.Entity.BookingStatus;
import com.SpringBoot.BookingService.Booking_service.Entity.RoomType;
import com.SpringBoot.BookingService.Booking_service.Security.Jwt.JwtFilter;
import com.SpringBoot.BookingService.Booking_service.Security.Jwt.JwtService;
import com.SpringBoot.BookingService.Booking_service.Service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters for simple controller endpoint testing
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    // Required because SecurityConfig references these beans during WebMvc context startup
    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    // --- Helper DTOs ---

    private BookingResponseDTO createSampleResponseDTO(Long bookingId, BookingStatus status) {
        return new BookingResponseDTO(
                bookingId,
                1L,
                RoomType.STANDARD,
                100L,
                1,
                List.of(new GuestDTO("John Doe", "PASSPORT", "P12345", 30)),
                LocalDate.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                status,
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("110.00")
        );
    }

    // ==========================================
    // 1. createBooking Endpoint (POST /bookings)
    // ==========================================

    @Test
    void testCreateBooking_Success() throws Exception {
        GuestDTO guest = new GuestDTO("John Doe", "PASSPORT", "P12345", 30);
        BookingRequestDTO request = new BookingRequestDTO(
                1L,
                RoomType.STANDARD,
                100L,
                1,
                List.of(guest),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        BookingResponseDTO response = createSampleResponseDTO(123L, BookingStatus.CONFIRMED);

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.finalAmount").value(110.00));
    }

    @Test
    void testCreateBooking_ValidationFailure_MissingHotelId() throws Exception {
        GuestDTO guest = new GuestDTO("John Doe", "PASSPORT", "P12345", 30);
        // hotelId is null
        BookingRequestDTO request = new BookingRequestDTO(
                null,
                RoomType.STANDARD,
                100L,
                1,
                List.of(guest),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBooking_ValidationFailure_EmptyGuests() throws Exception {
        // guests list is empty
        BookingRequestDTO request = new BookingRequestDTO(
                1L,
                RoomType.STANDARD,
                100L,
                1,
                Collections.emptyList(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // 2. cancelBooking Endpoint (POST /bookings/{id}/cancel)
    // ==========================================

    @Test
    void testCancelBooking_Success() throws Exception {
        Long bookingId = 123L;
        BookingResponseDTO response = createSampleResponseDTO(bookingId, BookingStatus.CANCELED);

        when(bookingService.cancelBooking(bookingId)).thenReturn(response);

        mockMvc.perform(post("/bookings/{bookingId}/cancel", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    // ==========================================
    // 3. getBooking Endpoint (GET /bookings/{id})
    // ==========================================

    @Test
    void testGetBooking_Success() throws Exception {
        Long bookingId = 123L;
        BookingResponseDTO response = createSampleResponseDTO(bookingId, BookingStatus.CONFIRMED);

        when(bookingService.getBooking(bookingId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    // ==========================================
    // 4. Query Endpoints
    // ==========================================

    @Test
    void testGetBookingsByStatus() throws Exception {
        BookingResponseDTO response = createSampleResponseDTO(123L, BookingStatus.CONFIRMED);
        when(bookingService.getBookingsByStatus(BookingStatus.CONFIRMED)).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/status/CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(123))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    void testGetBookingsByUserId() throws Exception {
        BookingResponseDTO response = createSampleResponseDTO(123L, BookingStatus.CONFIRMED);
        when(bookingService.getBookingsByUserId(100L)).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(123))
                .andExpect(jsonPath("$[0].userId").value(100));
    }

    @Test
    void testGetBookingsByHotelId() throws Exception {
        BookingResponseDTO response = createSampleResponseDTO(123L, BookingStatus.CONFIRMED);
        when(bookingService.getBookingsByHotelId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/hotel/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(123))
                .andExpect(jsonPath("$[0].hotelId").value(1));
    }

    // ==========================================
    // 5. checkIn / checkOut Endpoints
    // ==========================================

    @Test
    void testCheckIn_Success() throws Exception {
        Long bookingId = 123L;
        BookingResponseDTO response = createSampleResponseDTO(bookingId, BookingStatus.CHECKED_IN);

        when(bookingService.checkIn(bookingId)).thenReturn(response);

        mockMvc.perform(post("/bookings/{bookingId}/check-in", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.status").value("CHECKED_IN"));
    }

    @Test
    void testCheckOut_Success() throws Exception {
        Long bookingId = 123L;
        BookingResponseDTO response = createSampleResponseDTO(bookingId, BookingStatus.CHECKED_OUT);

        when(bookingService.checkOut(bookingId)).thenReturn(response);

        mockMvc.perform(post("/bookings/{bookingId}/check-out", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"));
    }
}
