package com.SpringBoot.InventoryService.Inventory_service.Repository;

import com.SpringBoot.InventoryService.Inventory_service.Entity.InventoryOperation;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryOperationRepository extends JpaRepository<InventoryOperation,String > {
}
