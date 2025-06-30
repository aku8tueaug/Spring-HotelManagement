package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryDTO;

import java.util.List;

public interface InventoryService {
    InventoryDTO createInventory(InventoryDTO inventoryDTO);
    InventoryDTO getInventoryById(Long id);
    List<InventoryDTO> getAllInventories();
    InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO);
    void deleteInventory(Long id);
    List<InventoryDTO> getInventoryByHotelId(Long hotelId);
    List<InventoryDTO> getInventoriesByRoomType(String roomType);
    void deductRooms(Long hotelId, String roomType, int count);
    void restoreRooms(Long hotelId, String roomType, int count);
    InventoryDTO getInventoryByHotelIdAndRoomType(Long hotelId, String roomType);

}
