package com.SpringBoot.InventoryService.Inventory_service.Validation;

import com.SpringBoot.InventoryService.Inventory_service.DTO.InventoryAvailabilityRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator
        implements ConstraintValidator<
        DateRangeValid,
        DateRangeRequest> {

    @Override
    public boolean isValid(DateRangeRequest dto, ConstraintValidatorContext constraintValidatorContext) {

        if (dto == null) {
            return true;
        }

        if (dto.startDate() == null
                || dto.endDate() == null) {
            return true;
        }

        return !dto.startDate()
                .isAfter(dto.endDate());
    }

}
