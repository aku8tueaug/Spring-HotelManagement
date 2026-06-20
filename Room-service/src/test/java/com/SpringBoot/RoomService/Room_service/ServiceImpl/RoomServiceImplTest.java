package com.SpringBoot.RoomService.Room_service.ServiceImpl;

import com.SpringBoot.RoomService.Room_service.DTO.*;
import com.SpringBoot.RoomService.Room_service.Entity.Room;
import com.SpringBoot.RoomService.Room_service.Entity.RoomStatus;
import com.SpringBoot.RoomService.Room_service.Entity.RoomType;
import com.SpringBoot.RoomService.Room_service.Exception.ResourceNotFoundException;
import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import com.SpringBoot.RoomService.Room_service.HTTPClient.InventoryClient;
import com.SpringBoot.RoomService.Room_service.Repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelClient hotelClient;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roomService, "defaultHorizonDays", 30);
    }

    // --- Helpers ---

    private Room createSampleRoom(Long id, String roomNumber, RoomType type, RoomStatus status) {
        return Room.builder()
                .roomId(id)
                .hotelId(1L)
                .roomNumber(roomNumber)
                .roomType(type)
                .status(status)
                .build();
    }

    // ==========================================
    // 1. addRoom() Tests
    // ==========================================

    @Test
    void testAddRoom_Active_Success() {
        CreateRoomRequestDTO request = new CreateRoomRequestDTO(1L, "101", RoomType.STANDARD, RoomStatus.ACTIVE);
        Room savedRoom = createSampleRoom(123L, "101", RoomType.STANDARD, RoomStatus.ACTIVE);

        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        ResponseRoomDTO response = roomService.addRoom(request);

        assertNotNull(response);
        assertEquals(123L, response.roomId());
        assertEquals(RoomStatus.ACTIVE, response.roomStatus());
        verify(hotelClient).validateHotelExists(1L);
        verify(inventoryClient).increaseInventory(any(InventoryAdjustmentRequestDTO.class));
        verify(inventoryClient, never()).blockInventory(any());
    }

    @Test
    void testAddRoom_Blocked_Success() {
        CreateRoomRequestDTO request = new CreateRoomRequestDTO(1L, "102", RoomType.DELUXE, RoomStatus.TEMPORARY_BLOCKED);
        Room savedRoom = createSampleRoom(124L, "102", RoomType.DELUXE, RoomStatus.TEMPORARY_BLOCKED);

        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        ResponseRoomDTO response = roomService.addRoom(request);

        assertNotNull(response);
        assertEquals(124L, response.roomId());
        assertEquals(RoomStatus.TEMPORARY_BLOCKED, response.roomStatus());
        verify(hotelClient).validateHotelExists(1L);
        verify(inventoryClient).increaseInventory(any());
        verify(inventoryClient).blockInventory(any());
    }

    // ==========================================
    // 2. addMultipleRoom() Tests
    // ==========================================

    @Test
    void testAddMultipleRoom_Success() {
        CreateMultipleRoomRequestDTO request = new CreateMultipleRoomRequestDTO(
                1L, RoomType.STANDARD, RoomStatus.ACTIVE, 1, 101, 3
        );

        when(roomRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<ResponseRoomDTO> responseList = roomService.addMultipleRoom(request);

        assertNotNull(responseList);
        assertEquals(3, responseList.size());
        assertEquals("101", responseList.get(0).roomNumber());
        assertEquals("102", responseList.get(1).roomNumber());
        assertEquals("103", responseList.get(2).roomNumber());
        verify(inventoryClient).increaseInventory(any());
        verify(roomRepository).saveAll(anyList());
    }

    // ==========================================
    // 3. deactivateRoomsByHotelId() Tests
    // ==========================================

    @Test
    void testDeactivateRoomsByHotelId_Success() {
        Long hotelId = 1L;
        List<Room> rooms = List.of(
                createSampleRoom(10L, "101", RoomType.STANDARD, RoomStatus.ACTIVE),
                createSampleRoom(11L, "102", RoomType.STANDARD, RoomStatus.TEMPORARY_BLOCKED),
                createSampleRoom(12L, "103", RoomType.DELUXE, RoomStatus.INACTIVE) // Already inactive
        );

        when(roomRepository.findByHotelId(hotelId)).thenReturn(rooms);

        roomService.deactivateRoomsByHotelId(hotelId);

        // Verify status updated to INACTIVE for all
        assertTrue(rooms.stream().allMatch(r -> r.getStatus() == RoomStatus.INACTIVE));
        // Verify inventory calls
        verify(inventoryClient, times(2)).decreaseInventory(argThat(dto -> dto.roomType() == RoomType.STANDARD && dto.count() == 1));
        verify(inventoryClient).unblockInventory(argThat(dto -> dto.roomType() == RoomType.STANDARD && dto.count() == 1));
        verify(roomRepository).saveAll(rooms);
    }

    // ==========================================
    // 4. reactivateRoomsByHotelId() Tests
    // ==========================================

    @Test
    void testReactivateRoomsByHotelId_Success() {
        Long hotelId = 1L;
        List<Room> rooms = List.of(
                createSampleRoom(10L, "101", RoomType.STANDARD, RoomStatus.INACTIVE),
                createSampleRoom(11L, "102", RoomType.DELUXE, RoomStatus.ACTIVE) // Already active
        );

        when(roomRepository.findByHotelId(hotelId)).thenReturn(rooms);

        roomService.reactivateRoomsByHotelId(hotelId);

        assertTrue(rooms.stream().allMatch(r -> r.getStatus() == RoomStatus.ACTIVE));
        verify(inventoryClient).increaseInventory(argThat(dto -> dto.roomType() == RoomType.STANDARD && dto.count() == 1));
        verify(roomRepository).saveAll(rooms);
    }

    // ==========================================
    // 5. updateRoom() Tests
    // ==========================================

    @Test
    void testUpdateRoom_SetInactiveManually_ThrowsIllegalArgumentException() {
        UpdateRoomRequestDTO request = new UpdateRoomRequestDTO(RoomType.STANDARD, RoomStatus.INACTIVE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.updateRoom(123L, request);
        });

        assertEquals("Room cannot be manually set to INACTIVE", exception.getMessage());
    }

    @Test
    void testUpdateRoom_TypeChangeActive_Success() {
        Long roomId = 123L;
        Room room = createSampleRoom(roomId, "101", RoomType.STANDARD, RoomStatus.ACTIVE);
        UpdateRoomRequestDTO request = new UpdateRoomRequestDTO(RoomType.DELUXE, null);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseRoomDTO response = roomService.updateRoom(roomId, request);

        assertNotNull(response);
        assertEquals(RoomType.DELUXE, response.roomType());
        verify(inventoryClient).decreaseInventory(argThat(dto -> dto.roomType() == RoomType.STANDARD));
        verify(inventoryClient).increaseInventory(argThat(dto -> dto.roomType() == RoomType.DELUXE));
    }

    @Test
    void testUpdateRoom_StatusChangeActiveToBlocked_Success() {
        Long roomId = 123L;
        Room room = createSampleRoom(roomId, "101", RoomType.STANDARD, RoomStatus.ACTIVE);
        UpdateRoomRequestDTO request = new UpdateRoomRequestDTO(null, RoomStatus.TEMPORARY_BLOCKED);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseRoomDTO response = roomService.updateRoom(roomId, request);

        assertNotNull(response);
        assertEquals(RoomStatus.TEMPORARY_BLOCKED, response.roomStatus());
        verify(inventoryClient).blockInventory(any());
    }

    @Test
    void testUpdateRoom_StatusChangeBlockedToActive_Success() {
        Long roomId = 123L;
        Room room = createSampleRoom(roomId, "101", RoomType.STANDARD, RoomStatus.TEMPORARY_BLOCKED);
        UpdateRoomRequestDTO request = new UpdateRoomRequestDTO(null, RoomStatus.ACTIVE);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseRoomDTO response = roomService.updateRoom(roomId, request);

        assertNotNull(response);
        assertEquals(RoomStatus.ACTIVE, response.roomStatus());
        verify(inventoryClient).unblockInventory(any());
    }

    // ==========================================
    // 6. Query and Simple Fetch Tests
    // ==========================================

    @Test
    void testGetRoomByRoomNumber_NotFound_ThrowsNoResourceFoundException() {
        when(roomRepository.findByRoomNumber("unknown")).thenReturn(Optional.empty());

        assertThrows(NoResourceFoundException.class, () -> {
            roomService.getRoomByRoomNumber("unknown");
        });
    }

    @Test
    void testGetRoomByHotelId_Success() {
        Long hotelId = 1L;
        when(roomRepository.findByHotelId(hotelId)).thenReturn(List.of(createSampleRoom(123L, "101", RoomType.STANDARD, RoomStatus.ACTIVE)));

        List<ResponseRoomDTO> rooms = roomService.getRoomByHotelId(hotelId);

        assertEquals(1, rooms.size());
        verify(hotelClient).validateHotelExists(hotelId);
    }
}
