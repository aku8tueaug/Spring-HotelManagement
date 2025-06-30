package com.SpringBoot.InventoryService.Inventory_service.Client;

import com.SpringBoot.InventoryService.Inventory_service.DTO.HotelDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface HotelClient {

    @GetExchange("/hotels/{id}")
    public HotelDTO getHotelById(@PathVariable("id") Long id);

}
