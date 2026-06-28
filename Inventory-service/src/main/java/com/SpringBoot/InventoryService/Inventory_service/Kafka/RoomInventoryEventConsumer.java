package com.SpringBoot.InventoryService.Inventory_service.Kafka;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAdjustmentRequestDTO;
import com.SpringBoot.InventoryService.Inventory_service.DTO.RoomInventoryEvent;
import com.SpringBoot.InventoryService.Inventory_service.Entity.InventoryOperation;
import com.SpringBoot.InventoryService.Inventory_service.Repository.InventoryOperationRepository;
import com.SpringBoot.InventoryService.Inventory_service.Service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomInventoryEventConsumer {

    private final InventoryOperationRepository inventoryOperationRepository;
    private final InventoryService inventoryService;


    @KafkaListener(
            topics = "room-inventory-events",
            groupId = "inventory-group"
    )
    @Transactional
    public void consume(RoomInventoryEvent event)
    {
        if(inventoryOperationRepository.existsById(event.operationId()))
        {
            log.info(
                    "Duplicate event attempted: {}",
                    event.operationId()
            );
            return;
        }
        log.info("Received Event: {}", event);
        switch (event.action())
        {
            case INCREASE ->
                    inventoryService.increaseInventory(
                            mapToInventoryAdjustmentRequestDTO(event)
                    );

            case DECREASE ->
                    inventoryService.decreaseInventory(
                            mapToInventoryAdjustmentRequestDTO(event)
                    );

            case BLOCK ->
                    inventoryService.blockInventory(
                            mapToInventoryAdjustmentRequestDTO(event)
                    );

            case UNBLOCK ->
                    inventoryService.unblockInventory(
                        mapToInventoryAdjustmentRequestDTO(event)
                    );
        }

        //After Successful Adjustment Operation, save the Operation in the inventoryOperation table
        InventoryOperation operation = new InventoryOperation();
        operation.setOperationId(event.operationId());
        operation.setProcessedAt(LocalDateTime.now());

        inventoryOperationRepository.save(operation);
    }


    //Helper Method
    private InventoryAdjustmentRequestDTO mapToInventoryAdjustmentRequestDTO
            ( RoomInventoryEvent event)
            {
                return  new InventoryAdjustmentRequestDTO(
                        event.hotelId(),
                        event.roomType(),
                        event.roomCount()
                );
            }
}