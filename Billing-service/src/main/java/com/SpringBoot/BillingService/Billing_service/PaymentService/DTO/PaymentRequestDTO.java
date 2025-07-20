package com.SpringBoot.BillingService.Billing_service.PaymentService.DTO;

public record PaymentRequestDTO(
        Long bookingId,
        String paymentMethod
) {
}
