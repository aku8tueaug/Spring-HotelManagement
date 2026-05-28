package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.*;
import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Exception.ResourceNotFoundException;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public void increaseInventory(InventoryAdjustmentRequestDTO request) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays( request.horizonDays() );

        List<Inventory> inventories = new ArrayList<>();

        for ( LocalDate date = startDate;
                !date.isAfter(endDate);
                date = date.plusDays(1)
             )
            {
                Inventory inventory =
                        inventoryRepository
                                .findByHotelIdAndRoomTypeAndInventoryDate(
                                        request.hotelId(),
                                        request.roomType(),
                                        date
                                )
                                .orElse(
                                        Inventory.builder()
                                                .hotelId(request.hotelId())
                                                .roomType(request.roomType())
                                                .inventoryDate(date)
                                                .totalRooms(0)
                                                .reservedRooms(0)
                                                .blockedRooms(0)
                                                .build()
                                );

                inventory.setTotalRooms(inventory.getTotalRooms() + request.count());
                inventories.add(inventory);
            }

        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void decreaseInventory(InventoryAdjustmentRequestDTO request) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays( request.horizonDays() );

        List<Inventory> inventories = new ArrayList<>();

        for ( LocalDate date = startDate;
              !date.isAfter(endDate);
              date = date.plusDays(1)
        )
        {
            Inventory inventory =
                    inventoryRepository
                            .findByHotelIdAndRoomTypeAndInventoryDate(
                                    request.hotelId(),
                                    request.roomType(),
                                    date
                            )
                            .orElseThrow(()->{ return new ResourceNotFoundException("Inventory Not found"); });

            int updatedTotal = inventory.getTotalRooms() - request.count();

            if(updatedTotal < 0 )
                throw new IllegalStateException("Inventory cannot become negative");

            inventory.setTotalRooms(updatedTotal);
            inventories.add(inventory);
        }
        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void blockInventory(InventoryReservationRequestDTO request) {

    }

    @Override
    public void unblockInventory(InventoryReservationRequestDTO request) {

    }

    @Override
    public void reserveInventory(InventoryReservationRequestDTO request) {

    }

    @Override
    public void releaseInventory(InventoryReservationRequestDTO request) {

    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInventoryByHotelAndRoomTypeAndDateRange
                                                            (Long hotelId,
                                                             RoomType roomType,
                                                             LocalDate startDate,
                                                             LocalDate endDate)
    {

        //get all the inventories between the date range
       List<Inventory> inventories = inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween
                                                                (hotelId, roomType, startDate, endDate);

       return entityToResponseDTO(inventories);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(InventoryAvailabilityRequestDTO request) {
        List<Inventory> inventories = inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween
                            (request.hotelId(), request.roomType(),request.startDate(),request.endDate());

        if(inventories.isEmpty())
            return  false;

        long totalDays = request.startDate().datesUntil(request.endDate()).count();

        return inventories.size() == totalDays;
    }

    //=========================== Helper Methods =================================================

    private InventoryResponseDTO entityToResponseDTO(Inventory inventory)
    {
        return new InventoryResponseDTO(
                inventory.getInventoryId(),
                inventory.getHotelId(),
                inventory.getRoomType(),
                inventory.getInventoryDate(),
                inventory.getTotalRooms(),
                inventory.getReservedRooms(),
                inventory.getBlockedRooms(),
                inventory.getAvailableRooms()
        );
    }

    private List<InventoryResponseDTO> entityToResponseDTO(List<Inventory> inventories)
    {
            return inventories.stream()
                    .map(this::entityToResponseDTO)
                    .toList();

    }

}

