package com.SpringBoot.InventoryService.Inventory_service.Controller;


import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final InventoryService inventoryService;

    @PatchMapping("/increase")
    public ResponseEntity<Void> increaseIventory (@RequestBody InventoryAdjustmentRequestDTO request)
    {
        inventoryService.increaseInventory(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/decrease")
    public ResponseEntity<?> removeIventory()
    {
        return new ResponseEntity<>(null); // dummy implementation
    }

    @PatchMapping("/block")
//    maintenance,cleaning,temporary unavailable
    public ResponseEntity<?> blockIventory()
    {
        return new ResponseEntity<>(null); // dummy implementation
    }

    @PatchMapping("/reserve")
//    Booking service will use this.
    public ResponseEntity<?> reserveIventory()
    {
//        reservedRooms += count
        return new ResponseEntity<>(null); // dummy implementation
    }


    @PatchMapping("/release")
//    Booking service will use this.
    public ResponseEntity<?> releaseIventory()
    {
//        reservedRooms -= count
        return new ResponseEntity<>(null); // dummy implementation
    }
}
