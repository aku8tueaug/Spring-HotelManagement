package com.SpringBoot.RoomService.Room_service.Controller;

import com.SpringBoot.RoomService.Room_service.DTO.*;
import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.Security.Jwt.JwtFilter;
import com.SpringBoot.RoomService.Room_service.Security.Jwt.JwtService;
import com.SpringBoot.RoomService.Room_service.Service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    // --- Helpers ---

    private ResponseRoomDTO createSampleResponse(Long roomId, String number) {
        return new ResponseRoomDTO(roomId, 1L, number, RoomType.STANDARD, RoomStatus.ACTIVE);
    }

    // ==========================================
    // 1. getAllRooms Tests (GET /rooms)
    // ==========================================

    @Test
    void testGetAllRooms_Success() throws Exception {
        ResponseRoomDTO response = createSampleResponse(123L, "101");
        when(roomService.getAllRooms()).thenReturn(List.of(response));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomId").value(123))
                .andExpect(jsonPath("$[0].roomNumber").value("101"));

        verify(roomService).getAllRooms();
    }

    // ==========================================
    // 2. addRoom Tests (POST /rooms)
    // ==========================================

    @Test
    void testAddRoom_Success() throws Exception {
        CreateRoomRequestDTO request = new CreateRoomRequestDTO(1L, "101", RoomType.STANDARD, RoomStatus.ACTIVE);
        ResponseRoomDTO response = createSampleResponse(123L, "101");

        when(roomService.addRoom(any(CreateRoomRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value(123));

        verify(roomService).addRoom(any(CreateRoomRequestDTO.class));
    }

    // ==========================================
    // 3. addMultipleRoom Tests (POST /rooms/batch)
    // ==========================================

    @Test
    void testAddMultipleRoom_Success() throws Exception {
        CreateMultipleRoomRequestDTO request = new CreateMultipleRoomRequestDTO(
                1L, RoomType.STANDARD, RoomStatus.ACTIVE, 1, 101, 2
        );
        List<ResponseRoomDTO> response = List.of(
                createSampleResponse(123L, "101"),
                createSampleResponse(124L, "102")
        );

        when(roomService.addMultipleRoom(any(CreateMultipleRoomRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/rooms/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].roomId").value(123))
                .andExpect(jsonPath("$[1].roomId").value(124));

        verify(roomService).addMultipleRoom(any(CreateMultipleRoomRequestDTO.class));
    }

    // ==========================================
    // 4. getRoomByRoomNumber Tests (GET /rooms/number/{roomNumber})
    // ==========================================

    @Test
    void testGetRoomByRoomNumber_Success() throws Exception {
        ResponseRoomDTO response = createSampleResponse(123L, "101");
        when(roomService.getRoomByRoomNumber("101")).thenReturn(response);

        mockMvc.perform(get("/rooms/number/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(123));

        verify(roomService).getRoomByRoomNumber("101");
    }

    // ==========================================
    // 5. updateRoom Tests (PATCH /rooms/{roomId})
    // ==========================================

    @Test
    void testUpdateRoom_Success() throws Exception {
        Long roomId = 123L;
        UpdateRoomRequestDTO request = new UpdateRoomRequestDTO(RoomType.DELUXE, RoomStatus.TEMPORARY_BLOCKED);
        ResponseRoomDTO response = new ResponseRoomDTO(roomId, 1L, "101", RoomType.DELUXE, RoomStatus.TEMPORARY_BLOCKED);

        when(roomService.updateRoom(eq(roomId), any(UpdateRoomRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomType").value("DELUXE"))
                .andExpect(jsonPath("$.roomStatus").value("TEMPORARY_BLOCKED"));

        verify(roomService).updateRoom(eq(roomId), any(UpdateRoomRequestDTO.class));
    }

    // ==========================================
    // 6. getRoomsByHotelAndType Tests (GET /rooms/hotel/{hotelId}/type/{roomType})
    // ==========================================

    @Test
    void testGetRoomsByHotelAndType_Success() throws Exception {
        RoomSummaryDTO summary = new RoomSummaryDTO(1L, RoomType.STANDARD, "101");
        when(roomService.getRoomByHotelIdAndRoomType(1L, RoomType.STANDARD)).thenReturn(List.of(summary));

        mockMvc.perform(get("/rooms/hotel/1/type/STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"));

        verify(roomService).getRoomByHotelIdAndRoomType(1L, RoomType.STANDARD);
    }
}
