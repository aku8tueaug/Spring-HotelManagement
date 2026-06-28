package com.SpringBoot.InventoryService.Inventory_service.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//Creating this Entity to store the operation details and also to Inventory Operation Idempotent.
//Idempotent Consumer Pattern
@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class InventoryOperation
{
    @Id
    private String operationId;
    private LocalDateTime processedAt;
}
