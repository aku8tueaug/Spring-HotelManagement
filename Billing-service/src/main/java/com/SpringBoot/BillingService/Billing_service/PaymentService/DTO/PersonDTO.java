package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;

import java.time.LocalDate;

public record PersonDTO(
        Long id,
        String name,
        LocalDate DOB,
        String Pincode,
        String Address,
        String mobileNumber
) {}
