package com.SpringBoot.RoomService.Room_service.HTTPClient;


import com.SpringBoot.RoomService.Room_service.DTO.InventoryAdjustmentRequestDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface InventoryClient {
    @PatchExchange("/internal/inventory/increase")
    public void increaseInventory(@RequestBody InventoryAdjustmentRequestDTO requestDTO) ;
}
