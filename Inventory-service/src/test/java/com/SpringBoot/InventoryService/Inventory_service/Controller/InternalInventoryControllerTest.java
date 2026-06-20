package com.SpringBoot.InventoryService.Inventory_service.Controller;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryReservationRequestDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalInventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InternalInventoryControllerTest {

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
    void testIncreaseInventory_Success() throws Exception {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 30);

        mockMvc.perform(patch("/internal/inventory/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).increaseInventory(any(InventoryAdjustmentRequestDTO.class));
    }

    @Test
    void testRemoveInventory_Success() throws Exception {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 30);

        mockMvc.perform(patch("/internal/inventory/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).decreaseInventory(any(InventoryAdjustmentRequestDTO.class));
    }

    @Test
    void testBlockInventory_Success() throws Exception {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 30);

        mockMvc.perform(patch("/internal/inventory/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).blockInventory(any(InventoryAdjustmentRequestDTO.class));
    }

    @Test
    void testUnblockInventory_Success() throws Exception {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 30);

        mockMvc.perform(patch("/internal/inventory/unblock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).unblockInventory(any(InventoryAdjustmentRequestDTO.class));
    }

    @Test
    void testReserveInventory_Success() throws Exception {
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(2), 2);

        mockMvc.perform(patch("/internal/inventory/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).reserveInventory(any(InventoryReservationRequestDTO.class));
    }

    @Test
    void testReleaseInventory_Success() throws Exception {
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(2), 2);

        mockMvc.perform(patch("/internal/inventory/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).releaseInventory(any(InventoryReservationRequestDTO.class));
    }
}
