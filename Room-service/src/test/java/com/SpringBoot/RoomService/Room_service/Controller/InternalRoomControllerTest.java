package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.Security.Jwt.JwtFilter;
import com.SpringBoot.RoomService.Room_service.Security.Jwt.JwtService;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InternalRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testDeactivateRooms_Success() throws Exception {
        Long hotelId = 1L;

        mockMvc.perform(patch("/internal/rooms/hotels/{hotelId}/deactivate", hotelId))
                .andExpect(status().isOk());

        verify(roomService).deactivateRoomsByHotelId(hotelId);
    }

    @Test
    void testReactivateRooms_Success() throws Exception {
        Long hotelId = 1L;

        mockMvc.perform(patch("/internal/rooms/hotels/{hotelId}/reactivate", hotelId))
                .andExpect(status().isOk());

        verify(roomService).reactivateRoomsByHotelId(hotelId);
    }
}
