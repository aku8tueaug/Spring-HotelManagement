package com.SpringBoot.InventoryService.Inventory_service.Controller;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAvailabilityRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryResponseDTO;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(@RequestBody InventoryAvailabilityRequestDTO request)
    {
        return ResponseEntity.ok(
                inventoryService.checkAvailability(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByDateRange(@RequestParam Long hotelId,
                                                                              @RequestParam RoomType roomType,
                                                                              @RequestParam LocalDate startDate,
                                                                              @RequestParam LocalDate endDate)
    {

        return ResponseEntity.ok(
                inventoryService.getInventoryByHotelAndRoomTypeAndDateRange(hotelId,roomType,startDate,endDate)
        );
    }


}

