package com.SpringBoot.InventoryService.Inventory_service.Controller;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAvailabilityRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryQueryRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryResponseDTO;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Security.Jwt.JwtFilter;
import com.SpringBoot.InventoryService.Inventory_service.Security.Jwt.JwtService;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testCheckAvailability_Success() throws Exception {
        InventoryAvailabilityRequestDTO request = new InventoryAvailabilityRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(1), 2);
        when(inventoryService.checkAvailability(any(InventoryAvailabilityRequestDTO.class))).thenReturn(true);

        mockMvc.perform(post("/inventory/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(inventoryService).checkAvailability(any(InventoryAvailabilityRequestDTO.class));
    }

    @Test
    void testGetInventoryByDateRange_Success() throws Exception {
        InventoryQueryRequestDTO request = new InventoryQueryRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(1));
        InventoryResponseDTO response = new InventoryResponseDTO(101L, 1L, RoomType.STANDARD, LocalDate.now(), 10, 2, 1, 7);

        when(inventoryService.getInventoryByHotelAndRoomTypeAndDateRange(any(InventoryQueryRequestDTO.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventoryId").value(101))
                .andExpect(jsonPath("$[0].availableRooms").value(7));

        verify(inventoryService).getInventoryByHotelAndRoomTypeAndDateRange(any(InventoryQueryRequestDTO.class));
    }
}
