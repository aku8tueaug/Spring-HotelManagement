package com.SpringBoot.InventoryService.Inventory_service.Controller;

import com.SpringBoot.InventoryService.Inventory_service.Client.HotelClient;
import com.SpringBoot.InventoryService.Inventory_service.Client.RoomClient;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryDTO;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService)
    {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(@RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.createInventory(inventoryDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(@PathVariable Long id, @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/hotels/{id}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByHotelId(@PathVariable("id") Long hotelId)
    {
        return ResponseEntity.ok(inventoryService.getInventoryByHotelId(hotelId));

    }
    @GetMapping("/roomType/{roomType}")
    public ResponseEntity<List<InventoryDTO>> getInventoriesByRoomType(@PathVariable("roomType") String roomType)
    {
        return ResponseEntity.ok(inventoryService.getInventoriesByRoomType(roomType));
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductRooms(
            @RequestParam Long hotelId,
            @RequestParam String roomType,
            @RequestParam int count) {
        inventoryService.deductRooms(hotelId, roomType, count);
        return ResponseEntity.ok("Rooms deducted successfully");
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreRooms(
            @RequestParam Long hotelId,
            @RequestParam String roomType,
            @RequestParam int count) {
        inventoryService.restoreRooms(hotelId, roomType, count);
        return ResponseEntity.ok("Rooms restored successfully");
    }

    @GetMapping("/hotelsAndRoomType")
    public ResponseEntity<InventoryDTO> getInventoryByHotelIdAndRoomType(
            @RequestParam Long hotelId,
            @RequestParam String roomType)
    {
        return ResponseEntity.ok(inventoryService.getInventoryByHotelIdAndRoomType(hotelId,roomType));
    }
}

