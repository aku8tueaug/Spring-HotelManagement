package com.SpringBoot.InventoryService.Inventory_service.Validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface DateRangeValid {
    String message() default
            "Start Date cannot be after end date";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
