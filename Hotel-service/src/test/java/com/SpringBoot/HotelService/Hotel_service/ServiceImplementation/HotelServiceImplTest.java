package com.SpringBoot.HotelService.Hotel_service.ServiceImplementation;

import com.SpringBoot.HotelService.Hotel_service.DTO.*;
import com.SpringBoot.HotelService.Hotel_service.Entity.Address;
import com.SpringBoot.HotelService.Hotel_service.Entity.Hotel;
import com.SpringBoot.HotelService.Hotel_service.Exception.ResourceNotFoundException;
import com.SpringBoot.HotelService.Hotel_service.HTTPClient.RoomClient;
import com.SpringBoot.HotelService.Hotel_service.Repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomClient roomClient;

    @InjectMocks
    private HotelServiceImpl hotelService;

    // --- Helper Methods ---

    private AddressDTO createSampleAddressDTO() {
        return new AddressDTO("123 Main St", "Delhi", "Delhi", "India", "110001");
    }

    private HotelCreateRequestDTO createSampleCreateDTO() {
        return new HotelCreateRequestDTO(
                "The Plaza",
                createSampleAddressDTO(),
                5,
                "Luxury Hotel",
                "9876543210",
                "info@theplaza.com",
                "http://example.com/image.jpg"
        );
    }

    private Hotel createSampleHotel(Long id) {
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Delhi");
        address.setState("Delhi");
        address.setCountry("India");
        address.setZipCode("110001");

        Hotel hotel = Hotel.builder()
                .id(id)
                .name("The Plaza")
                .address(address)
                .rating(5)
                .description("Luxury Hotel")
                .contactNumber("9876543210")
                .email("info@theplaza.com")
                .imageUrl("http://example.com/image.jpg")
                .active(true)
                .build();
        hotel.setCreatedAt(LocalDateTime.now());
        return hotel;
    }

    // ==========================================
    // 1. createHotel() Tests
    // ==========================================

    @Test
    void testCreateHotel_Success() {
        HotelCreateRequestDTO request = createSampleCreateDTO();
        Hotel hotelEntity = createSampleHotel(123L);

        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotelEntity);

        HotelResponseDTO response = hotelService.createHotel(request);

        assertNotNull(response);
        assertEquals(123L, response.id());
        assertEquals("The Plaza", response.name());
        assertEquals("Delhi", response.address().city());
        verify(hotelRepository).save(any(Hotel.class));
    }

    // ==========================================
    // 2. updateHotel() Tests
    // ==========================================

    @Test
    void testUpdateHotel_Success() {
        Long hotelId = 123L;
        Hotel existingHotel = createSampleHotel(hotelId);
        HotelUpdateRequestDTO updateRequest = HotelUpdateRequestDTO.builder()
                .name("New Plaza Name")
                .rating(4)
                .address(new AddressDTO("New St", "New City", "New State", "New Country", "20002"))
                .build();

        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.of(existingHotel));

        HotelResponseDTO response = hotelService.updateHotel(hotelId, updateRequest);

        assertNotNull(response);
        assertEquals("New Plaza Name", response.name());
        assertEquals(4, response.rating());
        assertEquals("New City", response.address().city());
        verify(hotelRepository).findByIdAndActiveTrue(hotelId);
    }

    @Test
    void testUpdateHotel_NotFound_ThrowsResourceNotFoundException() {
        Long hotelId = 123L;
        HotelUpdateRequestDTO updateRequest = new HotelUpdateRequestDTO();
        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hotelService.updateHotel(hotelId, updateRequest);
        });

        assertTrue(exception.getMessage().contains("Hotel not found with id: " + hotelId));
        verify(hotelRepository, never()).save(any());
    }

    // ==========================================
    // 3. getHotelById() Tests
    // ==========================================

    @Test
    void testGetHotelById_Success() {
        Long hotelId = 123L;
        Hotel hotel = createSampleHotel(hotelId);
        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.of(hotel));

        HotelResponseDTO response = hotelService.getHotelById(hotelId);

        assertNotNull(response);
        assertEquals(hotelId, response.id());
    }

    @Test
    void testGetHotelById_NotFound_ThrowsResourceNotFoundException() {
        Long hotelId = 123L;
        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            hotelService.getHotelById(hotelId);
        });
    }

    // ==========================================
    // 4. getAllHotels() Tests
    // ==========================================

    @Test
    void testGetAllHotels_Success() {
        Hotel hotel = createSampleHotel(123L);
        Page<Hotel> pageResult = new PageImpl<>(List.of(hotel));

        when(hotelRepository.findByActiveTrue(any(Pageable.class))).thenReturn(pageResult);

        Page<HotelResponseDTO> response = hotelService.getAllHotels(0, 10, "name", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("The Plaza", response.getContent().get(0).name());
        verify(hotelRepository).findByActiveTrue(any(Pageable.class));
    }

    // ==========================================
    // 5. deleteHotelById() Tests
    // ==========================================

    @Test
    void testDeleteHotelById_Success() {
        Long hotelId = 123L;
        Hotel hotel = createSampleHotel(hotelId);
        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.of(hotel));

        HotelResponseDTO response = hotelService.deleteHotelById(hotelId);

        assertNotNull(response);
        assertFalse(response.active());
        verify(roomClient).deactivateRoomsByHotelId(hotelId);
        verify(hotelRepository).updateActiveStatus(hotelId, false);
    }

    @Test
    void testDeleteHotelById_RoomDeactivationFails_ThrowsException() {
        Long hotelId = 123L;
        Hotel hotel = createSampleHotel(hotelId);
        when(hotelRepository.findByIdAndActiveTrue(hotelId)).thenReturn(Optional.of(hotel));
        doThrow(new RuntimeException("Room deactivation failed")).when(roomClient).deactivateRoomsByHotelId(hotelId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hotelService.deleteHotelById(hotelId);
        });

        assertEquals("Room deactivation failed", exception.getMessage());
        verify(hotelRepository, never()).updateActiveStatus(anyLong(), anyBoolean());
    }

    // ==========================================
    // 6. reactivateHotelById() Tests
    // ==========================================

    @Test
    void testReactivateHotelById_Success() {
        Long hotelId = 123L;
        Hotel hotel = createSampleHotel(hotelId);
        hotel.setActive(false);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        HotelResponseDTO response = hotelService.reactivateHotelById(hotelId);

        assertNotNull(response);
        assertTrue(response.active());
        verify(roomClient).reactivateRoomsByHotelId(hotelId);
        verify(hotelRepository).updateActiveStatus(hotelId, true);
    }
}
