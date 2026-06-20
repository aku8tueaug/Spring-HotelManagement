package com.SpringBoot.InventoryService.Inventory_service.Validation;

import java.time.LocalDate;

public interface DateRangeRequest {
    LocalDate startDate();
    LocalDate endDate();
}
