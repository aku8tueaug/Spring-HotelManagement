package com.SpringBoot.InventoryService.Inventory_service.Controller;


import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryReservationRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> increaseInventory(@RequestBody InventoryAdjustmentRequestDTO request)
    {
        inventoryService.increaseInventory(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/decrease")
    public ResponseEntity<Void> removeInventory(@RequestBody InventoryAdjustmentRequestDTO requestDTO)
    {
        inventoryService.decreaseInventory(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/block")
//    maintenance,cleaning,temporary unavailable
    public ResponseEntity<Void> blockInventory( @Valid
            @RequestBody InventoryAdjustmentRequestDTO requestDTO
    )
    {
       inventoryService.blockInventory(requestDTO);
       return ResponseEntity.ok().build();
    }

    @PatchMapping("/unblock")
//    maintenance,cleaning,temporary unavailable
    public ResponseEntity<Void> unblockInventory( @Valid
            @RequestBody InventoryAdjustmentRequestDTO requestDTO
    )
    {
        inventoryService.unblockInventory(requestDTO);
        return ResponseEntity.ok().build();
    }




    @PatchMapping("/reserve")
//    Booking service will use this.
    public ResponseEntity<Void> reserveInventory( @Valid
            @RequestBody InventoryReservationRequestDTO reservationRequestDTO)
    {
        inventoryService.reserveInventory(reservationRequestDTO);
//        reservedRooms += count
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/release")
//    Booking service will use this.
    public ResponseEntity<?> releaseInventory( @Valid
            @RequestBody InventoryReservationRequestDTO reservationRequestDTO )
    {
//        reservedRooms -= count
        inventoryService.releaseInventory(reservationRequestDTO);
        return ResponseEntity.ok().build();
    }
}
