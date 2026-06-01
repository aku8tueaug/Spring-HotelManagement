package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.*;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {

    void increaseInventory(InventoryAdjustmentRequestDTO request);

    void decreaseInventory(InventoryAdjustmentRequestDTO request);

    void blockInventory(InventoryAdjustmentRequestDTO request);

    void unblockInventory(InventoryAdjustmentRequestDTO request);

    void reserveInventory(InventoryReservationRequestDTO request);

    void releaseInventory(InventoryReservationRequestDTO request);

    List<InventoryResponseDTO>
    getInventoryByHotelAndRoomTypeAndDateRange(InventoryQueryRequestDTO requestDTO );

    boolean checkAvailability(
            InventoryAvailabilityRequestDTO request
    );

}
