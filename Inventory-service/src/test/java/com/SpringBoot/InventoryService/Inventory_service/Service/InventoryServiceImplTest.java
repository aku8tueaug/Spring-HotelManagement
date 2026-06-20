package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.*;
import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Exception.InsufficientInventoryException;
import com.SpringBoot.InventoryService.Inventory_service.Exception.ResourceNotFoundException;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory createSampleInventory(Long id, LocalDate date, int total, int reserved, int blocked) {
        return Inventory.builder()
                .inventoryId(id)
                .hotelId(1L)
                .roomType(RoomType.STANDARD)
                .inventoryDate(date)
                .totalRooms(total)
                .reservedRooms(reserved)
                .blockedRooms(blocked)
                .build();
    }

    // ==========================================
    // 1. increaseInventory() Tests
    // ==========================================

    @Test
    void testIncreaseInventory_Success() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 2);
        LocalDate today = LocalDate.now();
        Inventory existing = createSampleInventory(101L, today, 10, 2, 1);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        inventoryService.increaseInventory(request);

        assertEquals(15, existing.getTotalRooms());
        verify(inventoryRepository).saveAll(anyList());
    }

    // ==========================================
    // 2. decreaseInventory() Tests
    // ==========================================

    @Test
    void testDecreaseInventory_NotFound_ThrowsResourceNotFoundException() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 1);
        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.decreaseInventory(request);
        });
    }

    @Test
    void testDecreaseInventory_NegativeTotal_ThrowsIllegalStateException() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 10, 1);
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 5, 0, 0);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            inventoryService.decreaseInventory(request);
        });
        assertEquals("Inventory cannot become negative", ex.getMessage());
    }

    @Test
    void testDecreaseInventory_BelowReservedAndBlocked_ThrowsIllegalStateException() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 1);
        // Total is 10. Reserved = 4, Blocked = 2. Remaining = 4.
        // Reducing by 5 would make total 5, which is < reserved + blocked (6).
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 10, 4, 2);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            inventoryService.decreaseInventory(request);
        });
        assertEquals("Cannot reduce inventory below reserved/blocked rooms", ex.getMessage());
    }

    @Test
    void testDecreaseInventory_Success() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 3, 1);
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 10, 4, 2);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        inventoryService.decreaseInventory(request);

        assertEquals(7, existing.getTotalRooms());
        verify(inventoryRepository).saveAll(anyList());
    }

    // ==========================================
    // 3. blockInventory() Tests
    // ==========================================

    @Test
    void testBlockInventory_InsufficientRooms_ThrowsInsufficientInventoryException() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 1);
        // Total = 10, Reserved = 8, Blocked = 0 -> Available = 2.
        // Requesting block of 5 is invalid.
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 10, 8, 0);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        assertThrows(InsufficientInventoryException.class, () -> {
            inventoryService.blockInventory(request);
        });
    }

    @Test
    void testBlockInventory_Success() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 2, 1);
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 10, 4, 1);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        inventoryService.blockInventory(request);

        assertEquals(3, existing.getBlockedRooms());
        verify(inventoryRepository).saveAll(anyList());
    }

    // ==========================================
    // 4. unblockInventory() Tests
    // ==========================================

    @Test
    void testUnblockInventory_NegativeBlocked_ThrowsIllegalStateException() {
        InventoryAdjustmentRequestDTO request = new InventoryAdjustmentRequestDTO(1L, RoomType.STANDARD, 5, 1);
        Inventory existing = createSampleInventory(101L, LocalDate.now(), 10, 4, 2);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                eq(1L), eq(RoomType.STANDARD), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> {
            inventoryService.unblockInventory(request);
        });
    }

    // ==========================================
    // 5. reserveInventory() Tests
    // ==========================================

    @Test
    void testReserveInventory_MissingDate_ThrowsInsufficientInventoryException() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2); // 3 days total
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, start, end, 2);

        // Only returns 2 inventories instead of 3
        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 0, 0),
                        createSampleInventory(102L, start.plusDays(1), 10, 0, 0)
                ));

        assertThrows(InsufficientInventoryException.class, () -> {
            inventoryService.reserveInventory(request);
        });
    }

    @Test
    void testReserveInventory_Insufficient_ThrowsInsufficientInventoryException() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, start, end, 5);

        // Available rooms = 4 for first day, which is less than 5
        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 6, 0),
                        createSampleInventory(102L, start.plusDays(1), 10, 0, 0)
                ));

        assertThrows(InsufficientInventoryException.class, () -> {
            inventoryService.reserveInventory(request);
        });
    }

    @Test
    void testReserveInventory_Success() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, start, end, 3);
        Inventory inv1 = createSampleInventory(101L, start, 10, 2, 0);
        Inventory inv2 = createSampleInventory(102L, start.plusDays(1), 10, 1, 0);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(inv1, inv2));

        inventoryService.reserveInventory(request);

        assertEquals(5, inv1.getReservedRooms());
        assertEquals(4, inv2.getReservedRooms());
        verify(inventoryRepository).saveAll(anyList());
    }

    // ==========================================
    // 6. releaseInventory() Tests
    // ==========================================

    @Test
    void testReleaseInventory_NegativeReserved_ThrowsIllegalStateException() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        InventoryReservationRequestDTO request = new InventoryReservationRequestDTO(1L, RoomType.STANDARD, start, end, 5);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 2, 0),
                        createSampleInventory(102L, start.plusDays(1), 10, 6, 0)
                ));

        assertThrows(IllegalStateException.class, () -> {
            inventoryService.releaseInventory(request);
        });
    }

    // ==========================================
    // 7. checkAvailability() Tests
    // ==========================================

    @Test
    void testCheckAvailability_MissingDatesRange_ReturnsFalse() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2); // 3 days
        InventoryAvailabilityRequestDTO request = new InventoryAvailabilityRequestDTO(1L, RoomType.STANDARD, start, end, 2);

        // Only 2 records present
        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 0, 0),
                        createSampleInventory(102L, start.plusDays(1), 10, 0, 0)
                ));

        assertFalse(inventoryService.checkAvailability(request));
    }

    @Test
    void testCheckAvailability_InsufficientRooms_ReturnsFalse() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        InventoryAvailabilityRequestDTO request = new InventoryAvailabilityRequestDTO(1L, RoomType.STANDARD, start, end, 4);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 7, 0), // available = 3
                        createSampleInventory(102L, start.plusDays(1), 10, 0, 0) // available = 10
                ));

        assertFalse(inventoryService.checkAvailability(request));
    }

    @Test
    void testCheckAvailability_Success_ReturnsTrue() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        InventoryAvailabilityRequestDTO request = new InventoryAvailabilityRequestDTO(1L, RoomType.STANDARD, start, end, 4);

        when(inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(1L, RoomType.STANDARD, start, end))
                .thenReturn(List.of(
                        createSampleInventory(101L, start, 10, 5, 0), // available = 5
                        createSampleInventory(102L, start.plusDays(1), 10, 2, 1) // available = 7
                ));

        assertTrue(inventoryService.checkAvailability(request));
    }
}
