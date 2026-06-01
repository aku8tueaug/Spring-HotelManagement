package com.SpringBoot.InventoryService.Inventory_service.Exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InsufficientInventoryException extends RuntimeException{

    public InsufficientInventoryException(String msg)
    {
        super(msg);
    }
}
