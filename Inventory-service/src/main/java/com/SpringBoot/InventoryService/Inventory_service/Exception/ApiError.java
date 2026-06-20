package com.SpringBoot.InventoryService.Inventory_service.Exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details;
}
