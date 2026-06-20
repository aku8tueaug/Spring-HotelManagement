package com.SpringBoot.InventoryService.Inventory_service.Service;

import com.SpringBoot.InventoryService.Inventory_service.DTO.*;
import com.SpringBoot.InventoryService.Inventory_service.Entity.Inventory;
import com.SpringBoot.InventoryService.Inventory_service.Entity.RoomType;
import com.SpringBoot.InventoryService.Inventory_service.Exception.InsufficientInventoryException;
import com.SpringBoot.InventoryService.Inventory_service.Exception.ResourceNotFoundException;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public void increaseInventory(InventoryAdjustmentRequestDTO request) {
        log.info("ENTERED INCREASE");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.horizonDays() - 1);

        List<Inventory> existingInventories = inventoryRepository
                .findByHotelIdAndRoomTypeAndInventoryDateBetween(
                        request.hotelId(),
                        request.roomType(),
                        startDate,
                        endDate
                );

        Map<LocalDate, Inventory> inventoryMap = existingInventories.stream()
                .collect(Collectors.toMap(Inventory::getInventoryDate, inv -> inv));

        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = inventoryMap.get(date);
            if (inventory == null) {
                inventory = Inventory.builder()
                        .hotelId(request.hotelId())
                        .roomType(request.roomType())
                        .inventoryDate(date)
                        .totalRooms(0)
                        .reservedRooms(0)
                        .blockedRooms(0)
                        .build();
            }

            inventory.setTotalRooms(inventory.getTotalRooms() + request.count());
            inventoriesToSave.add(inventory);
        }

        inventoryRepository.saveAll(inventoriesToSave);
    }

    @Override
    public void decreaseInventory(InventoryAdjustmentRequestDTO request) {
        log.info("ENTERED DECREASE");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.horizonDays() - 1);

        List<Inventory> existingInventories = inventoryRepository
                .findByHotelIdAndRoomTypeAndInventoryDateBetween(
                        request.hotelId(),
                        request.roomType(),
                        startDate,
                        endDate
                );

        Map<LocalDate, Inventory> inventoryMap = existingInventories.stream()
                .collect(Collectors.toMap(Inventory::getInventoryDate, inv -> inv));

        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = inventoryMap.get(date);
            if (inventory == null) {
                throw new ResourceNotFoundException("Inventory Not found for date: " + date);
            }

            int updatedTotal = inventory.getTotalRooms() - request.count();

            if (updatedTotal < 0) {
                throw new IllegalStateException("Inventory cannot become negative");
            }

            if (updatedTotal < inventory.getReservedRooms() + inventory.getBlockedRooms()) {
                throw new IllegalStateException(
                        "Cannot reduce inventory below reserved/blocked rooms"
                );
            }

            inventory.setTotalRooms(updatedTotal);
            inventoriesToSave.add(inventory);
        }
        inventoryRepository.saveAll(inventoriesToSave);
    }

    @Override
    public void blockInventory(InventoryAdjustmentRequestDTO request) {
        log.info("ENTERED BLOCK");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.horizonDays() - 1);

        List<Inventory> existingInventories = inventoryRepository
                .findByHotelIdAndRoomTypeAndInventoryDateBetween(
                        request.hotelId(),
                        request.roomType(),
                        startDate,
                        endDate
                );

        Map<LocalDate, Inventory> inventoryMap = existingInventories.stream()
                .collect(Collectors.toMap(Inventory::getInventoryDate, inv -> inv));

        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = inventoryMap.get(date);
            if (inventory == null) {
                inventory = Inventory.builder()
                        .hotelId(request.hotelId())
                        .roomType(request.roomType())
                        .inventoryDate(date)
                        .totalRooms(0)
                        .reservedRooms(0)
                        .blockedRooms(0)
                        .build();
            }

            if (inventory.getAvailableRooms() < request.count()) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory available"
                );
            }

            inventory.setBlockedRooms(
                    inventory.getBlockedRooms() + request.count()
            );
            inventoriesToSave.add(inventory);
        }

        inventoryRepository.saveAll(inventoriesToSave);
    }

    @Override
    public void unblockInventory(InventoryAdjustmentRequestDTO request) {
        log.info("ENTERED UNBLOCK");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.horizonDays() - 1);

        List<Inventory> existingInventories = inventoryRepository
                .findByHotelIdAndRoomTypeAndInventoryDateBetween(
                        request.hotelId(),
                        request.roomType(),
                        startDate,
                        endDate
                );

        Map<LocalDate, Inventory> inventoryMap = existingInventories.stream()
                .collect(Collectors.toMap(Inventory::getInventoryDate, inv -> inv));

        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = inventoryMap.get(date);
            if (inventory == null) {
                throw new ResourceNotFoundException("Inventory Not found for date: " + date);
            }

            int updatedBlocked = inventory.getBlockedRooms() - request.count();

            if (updatedBlocked < 0) {
                throw new IllegalStateException("Inventory cannot become negative");
            }

            inventory.setBlockedRooms(updatedBlocked);
            inventoriesToSave.add(inventory);
        }
        inventoryRepository.saveAll(inventoriesToSave);
    }

    @Override
    public void reserveInventory(InventoryReservationRequestDTO request) {
        List<Inventory> inventories = fetchAndValidateAvailability(
                request.hotelId(), request.roomType(),
                request.startDate(), request.endDate() );

        boolean unavailable =
                inventories.stream()
                        .anyMatch(inventory -> inventory.getAvailableRooms()
                                < request.roomCount()
                        );
        if(unavailable)
        {
            throw new InsufficientInventoryException(
                    "Insufficient inventory available"
            );
        }

        for(Inventory inventory : inventories)
        {
            inventory.setReservedRooms(
                    inventory.getReservedRooms() + request.roomCount()
            );
        }

        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void releaseInventory(InventoryReservationRequestDTO request) {

        List<Inventory> inventories = fetchAndValidateAvailability(
                request.hotelId(), request.roomType(),
                request.startDate(), request.endDate() );

        for(Inventory inventory : inventories)
        {
            int updatedReserved =
                    inventory.getReservedRooms() - request.roomCount();

            if(updatedReserved < 0)
            {
                throw new IllegalStateException(
                        "Reserved Inventory cannot be negative"
                );
            }
            inventory.setReservedRooms(updatedReserved);
        }

        inventoryRepository.saveAll(inventories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInventoryByHotelAndRoomTypeAndDateRange
                                                            (InventoryQueryRequestDTO requestDTO)
    {

        //get all the inventories between the date range
       List<Inventory> inventories = inventoryRepository
               .findByHotelIdAndRoomTypeAndInventoryDateBetween
                    ( requestDTO.hotelId(),
                      requestDTO.roomType(),
                      requestDTO.startDate(),
                      requestDTO.endDate());

       return entityToResponseDTO(inventories);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(InventoryAvailabilityRequestDTO request) {
        List<Inventory> inventories = inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween(
                request.hotelId(), request.roomType(),
                request.startDate(), request.endDate() );

        long totalDays =
                request.startDate().datesUntil(
                        request.endDate().plusDays(1)).count();

        if (inventories.size() != totalDays) {
            return false;
        }

        return inventories.stream()
                .allMatch(inventory -> inventory.getAvailableRooms()
                        >= request.requiredRooms()
                );
    }





    //=========================== Helper Methods =================================================

    private List<Inventory> fetchAndValidateAvailability(
                                            Long hotelId,
                                            RoomType roomType,
                                            LocalDate startDate,
                                            LocalDate endDate)
    {
        List<Inventory> inventories = inventoryRepository.findByHotelIdAndRoomTypeAndInventoryDateBetween
                (hotelId,roomType,startDate,endDate);

        long totalDays =
                startDate.datesUntil(endDate.plusDays(1)).count();

        if (inventories.size() != totalDays) {
            throw new InsufficientInventoryException(
                    "Inventory missing for some dates between "
                            + startDate + " and " + endDate
            );
        }
        return inventories;
    }

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

