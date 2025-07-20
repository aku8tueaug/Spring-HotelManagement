package com.SpringBoot.InventoryService.Inventory_service.Client;

import com.SpringBoot.InventoryService.Inventory_service.Configuration.RoomClientConfiguration;
import com.SpringBoot.InventoryService.Inventory_service.DTO.RoomDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RoomClient {

    @GetExchange("/rooms/{roomType}")
    public RoomDTO getRoomByType(@PathVariable("roomType") String roomType);
}
