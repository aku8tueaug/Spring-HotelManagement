package com.SpringBoot.HotelService.Hotel_service.controller;

import com.SpringBoot.HotelService.Hotel_service.DTO.*;
import com.SpringBoot.HotelService.Hotel_service.Security.Jwt.JwtFilter;
import com.SpringBoot.HotelService.Hotel_service.Security.Jwt.JwtService;
import com.SpringBoot.HotelService.Hotel_service.Service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HotelService hotelService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    // --- Helpers ---

    private HotelResponseDTO createSampleResponseDTO(Long id) {
        AddressDTO address = new AddressDTO("123 Main St", "Delhi", "Delhi", "India", "110001");
        return new HotelResponseDTO(
                id,
                "The Plaza",
                address,
                5,
                "Luxury Hotel",
                "9876543210",
                "info@theplaza.com",
                "http://example.com/image.jpg",
                true,
                LocalDateTime.now()
        );
    }

    // ==========================================
    // 1. createHotel Tests
    // ==========================================

    @Test
    void testCreateHotel_Success() throws Exception {
        AddressDTO address = new AddressDTO("123 Main St", "Delhi", "Delhi", "India", "110001");
        HotelCreateRequestDTO request = new HotelCreateRequestDTO(
                "The Plaza", address, 5, "Luxury", "9876543210", "info@plaza.com", "http://image.jpg"
        );
        HotelResponseDTO response = createSampleResponseDTO(123L);

        when(hotelService.createHotel(any(HotelCreateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.name").value("The Plaza"));

        verify(hotelService).createHotel(any(HotelCreateRequestDTO.class));
    }

    @Test
    void testCreateHotel_ValidationFailure_BlankName() throws Exception {
        AddressDTO address = new AddressDTO("123 Main St", "Delhi", "Delhi", "India", "110001");
        // name is empty/blank
        HotelCreateRequestDTO request = new HotelCreateRequestDTO(
                "", address, 5, "Luxury", "9876543210", "info@plaza.com", "http://image.jpg"
        );

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // 2. getHotelById Tests
    // ==========================================

    @Test
    void testGetHotelById_Success() throws Exception {
        Long hotelId = 123L;
        HotelResponseDTO response = createSampleResponseDTO(hotelId);

        when(hotelService.getHotelById(hotelId)).thenReturn(response);

        mockMvc.perform(get("/hotels/{id}", hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.name").value("The Plaza"));

        verify(hotelService).getHotelById(hotelId);
    }

    // ==========================================
    // 3. updateHotel (PATCH) Tests
    // ==========================================

    @Test
    void testUpdateHotel_Success() throws Exception {
        Long hotelId = 123L;
        HotelUpdateRequestDTO updateRequest = HotelUpdateRequestDTO.builder()
                .name("New Plaza")
                .rating(4)
                .build();
        HotelResponseDTO response = createSampleResponseDTO(hotelId);

        when(hotelService.updateHotel(eq(hotelId), any(HotelUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/hotels/{id}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(hotelService).updateHotel(eq(hotelId), any(HotelUpdateRequestDTO.class));
    }

    // ==========================================
    // 4. getAllHotels Tests
    // ==========================================

    @Test
    void testGetAllHotels_Success() throws Exception {
        HotelResponseDTO response = createSampleResponseDTO(123L);
        Page<HotelResponseDTO> page = new PageImpl<>(List.of(response));

        when(hotelService.getAllHotels(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get("/hotels")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("The Plaza"));

        verify(hotelService).getAllHotels(0, 10, "id", "asc");
    }

    // ==========================================
    // 5. deleteHotelById Tests
    // ==========================================

    @Test
    void testDeleteHotelById_Success() throws Exception {
        Long hotelId = 123L;
        HotelResponseDTO response = createSampleResponseDTO(hotelId);

        when(hotelService.deleteHotelById(hotelId)).thenReturn(response);

        mockMvc.perform(delete("/hotels/{id}", hotelId))
                .andExpect(status().isOk());

        verify(hotelService).deleteHotelById(hotelId);
    }

    // ==========================================
    // 6. reactivateHotelById Tests
    // ==========================================

    @Test
    void testReactivateHotelById_Success() throws Exception {
        Long hotelId = 123L;
        HotelResponseDTO response = createSampleResponseDTO(hotelId);

        when(hotelService.reactivateHotelById(hotelId)).thenReturn(response);

        mockMvc.perform(post("/hotels/{id}", hotelId))
                .andExpect(status().isOk());

        verify(hotelService).reactivateHotelById(hotelId);
    }
}
