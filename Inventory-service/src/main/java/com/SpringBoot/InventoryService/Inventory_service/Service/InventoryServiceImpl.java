package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.Client.HotelClient;
import com.SpringBoot.InventoryService.Inventory_service.Client.RoomClient;
import com.SpringBoot.InventoryService.Inventory_service.DTO.HotelDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.RoomDTO;
import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                HotelClient hotelClient,
                                RoomClient roomClient) {
        this.inventoryRepository = inventoryRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
    }

    @Override
    public InventoryDTO createInventory(InventoryDTO dto) {
        checkExternalDetails(dto.getHotelId(), dto.getRoomType());
        Inventory inventory = mapToEntity(dto);
        return mapToDTO(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        return mapToDTO(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO updateInventory(Long id, InventoryDTO dto) {
        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        checkExternalDetails(dto.getHotelId(), dto.getRoomType());
        existing.setHotelId(dto.getHotelId());
        existing.setRoomType(dto.getRoomType());
        existing.setTotalRooms(dto.getTotalRooms());
        existing.setAvailableRooms(dto.getAvailableRooms());

        return mapToDTO(inventoryRepository.save(existing));
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public List<InventoryDTO> getInventoryByHotelId(Long hotelId)
    {
        checkExternalDetails(hotelId,null);
        List<Inventory> inventories= inventoryRepository.getInventoryByHotelId(hotelId)
                .orElseThrow(()->new NoSuchElementException("No data exist with provided hotel id"));
        return inventories.stream()
                        .map(this::mapToDTO)
                .toList() ;
    }

    @Override
    public List<InventoryDTO> getInventoriesByRoomType(String roomType) {
        RoomType roomType1  = RoomType.valueOf(roomType.toUpperCase());
        List<Inventory> inventories= inventoryRepository.getInventoriesByRoomType(roomType1)
                .orElseThrow(()->new NoSuchElementException("No data exist with provided hotel id"));
        return inventories.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void deductRooms(Long hotelId, String roomType, int count) {
        RoomType roomType1 = RoomType.valueOf(roomType.toUpperCase());
        Inventory inventory = inventoryRepository.findByHotelIdAndRoomType(hotelId, roomType1)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found"));

        if (inventory.getAvailableRooms() < count) {
            throw new IllegalArgumentException("Not enough rooms available");
        }

        inventory.setAvailableRooms(inventory.getAvailableRooms() - count);
        inventoryRepository.save(inventory);
    }

    @Override
    public void restoreRooms(Long hotelId, String roomType, int count) {
        RoomType roomType1 = RoomType.valueOf(roomType.toUpperCase());
        Inventory inventory = inventoryRepository.findByHotelIdAndRoomType(hotelId, roomType1)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found"));

        int total = inventory.getTotalRooms();
        int available = inventory.getAvailableRooms();

        if (available + count > total) {
            throw new IllegalArgumentException("Cannot exceed total rooms");
        }

        inventory.setAvailableRooms(available + count);
        inventoryRepository.save(inventory);
    }

    @Override
    public InventoryDTO getInventoryByHotelIdAndRoomType(Long hotelId, String roomType) {
        RoomType roomType1 = RoomType.valueOf(roomType.toUpperCase());
        Inventory inventory = inventoryRepository.findByHotelIdAndRoomType(hotelId,roomType1)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found"));
        return mapToDTO(inventory);
    }


    // Helper methods
    private Inventory mapToEntity(InventoryDTO dto) {
        return Inventory.builder()
                .inventoryId(dto.getInventoryId())
                .hotelId(dto.getHotelId())
                .roomType(dto.getRoomType())
                .totalRooms(dto.getTotalRooms())
                .availableRooms(dto.getAvailableRooms())
                .build();
    }

    private InventoryDTO mapToDTO(Inventory inventory) {
        return new InventoryDTO(
                inventory.getInventoryId(),
                inventory.getHotelId(),
                inventory.getRoomType(),
                inventory.getTotalRooms(),
                inventory.getAvailableRooms()
        );

    }
    public void checkExternalDetails(Long hotelId, RoomType roomId) {
        try {
            HotelDTO hotel;
            RoomDTO room;
            if(hotelId!=null)
                hotel = hotelClient.getHotelById(hotelId);
            if(roomId!=null)
            {
                String roomId1 = roomId.toString();
                room = roomClient.getRoomByType(roomId1);
            }

//            System.out.println("Hotel: " + hotel.name());
//            System.out.println("Room: " + room.roomNumber());
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Invalid hotel or room ID");
        }
    }
}

