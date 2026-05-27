package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryDTO;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {

    void increaseInventory(InventoryAdjustmentRequestDTO request);

    void decreaseInventory(InventoryAdjustmentRequestDTO request);

    void blockInventory(InventoryAdjustmentRequestDTO request);

    void unblockInventory(InventoryAdjustmentRequestDTO request);

    void reserveInventory(

            Long hotelId,
            RoomType roomType,
            LocalDate startDate,
            LocalDate endDate,
            Integer count
    );

    void releaseInventory(

            Long hotelId,
            RoomType roomType,
            LocalDate startDate,
            LocalDate endDate,
            Integer count
    );

    List<InventoryDTO> getAvailability(

            Long hotelId,
            RoomType roomType,
            LocalDate startDate,
            LocalDate endDate
    );

}
