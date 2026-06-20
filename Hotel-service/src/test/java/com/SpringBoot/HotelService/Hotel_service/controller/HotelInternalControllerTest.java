package com.SpringBoot.HotelService.Hotel_service.controller;

import com.SpringBoot.HotelService.Hotel_service.DTO.AddressDTO;
import com.SpringBoot.HotelService.Hotel_service.DTO.HotelResponseDTO;
import com.SpringBoot.HotelService.Hotel_service.Exception.ResourceNotFoundException;
import com.SpringBoot.HotelService.Hotel_service.Security.Jwt.JwtFilter;
import com.SpringBoot.HotelService.Hotel_service.Security.Jwt.JwtService;
import com.SpringBoot.HotelService.Hotel_service.Service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HotelInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testHotelExist_Success() throws Exception {
        Long hotelId = 123L;
        AddressDTO address = new AddressDTO("123 Main St", "Delhi", "Delhi", "India", "110001");
        HotelResponseDTO response = new HotelResponseDTO(
                hotelId, "The Plaza", address, 5, "Luxury", "9876543210", "info@plaza.com", "http://image.jpg", true, LocalDateTime.now()
        );

        when(hotelService.getHotelById(hotelId)).thenReturn(response);

        mockMvc.perform(get("/internal/hotels/{id}/exists", hotelId))
                .andExpect(status().isOk());

        verify(hotelService).getHotelById(hotelId);
    }

    @Test
    void testHotelExist_NotFound_Returns404() throws Exception {
        Long hotelId = 123L;
        when(hotelService.getHotelById(hotelId)).thenThrow(new ResourceNotFoundException("Hotel not found"));

        mockMvc.perform(get("/internal/hotels/{id}/exists", hotelId))
                .andExpect(status().isNotFound());

        verify(hotelService).getHotelById(hotelId);
    }
}
