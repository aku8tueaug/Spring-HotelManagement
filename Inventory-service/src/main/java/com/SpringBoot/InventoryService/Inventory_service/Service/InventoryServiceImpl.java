package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryDTO;
import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public void increaseInventory(InventoryAdjustmentRequestDTO request) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays( request.horizonDays() );

        List<Inventory> inventories = new ArrayList<>();

        for ( LocalDate date = startDate;
                !date.isAfter(endDate);
                date = date.plusDays(1)
             )
            {

                Inventory inventory =
                        inventoryRepository
                                .findByHotelIdAndRoomTypeAndInventoryDate(
                                        request.hotelId(),
                                        request.roomType(),
                                        date
                                )
                                .orElse(
                                        Inventory.builder()
                                                .hotelId(request.hotelId())
                                                .roomType(request.roomType())
                                                .inventoryDate(date)
                                                .totalRooms(0)
                                                .reservedRooms(0)
                                                .blockedRooms(0)
                                                .build()
                                );

                inventory.setTotalRooms(inventory.getTotalRooms() + request.count());
                inventories.add(inventory);
            }
        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void decreaseInventory(InventoryAdjustmentRequestDTO request) {

    }

    @Override
    public void blockInventory(InventoryAdjustmentRequestDTO request) {

    }

    @Override
    public void unblockInventory(InventoryAdjustmentRequestDTO request) {

    }

    @Override
    public void reserveInventory(Long hotelId, RoomType roomType, LocalDate startDate, LocalDate endDate, Integer count) {

    }

    @Override
    public void releaseInventory(Long hotelId, RoomType roomType, LocalDate startDate, LocalDate endDate, Integer count) {

    }

    @Override
    public List<InventoryDTO> getAvailability(Long hotelId, RoomType roomType, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }
}

