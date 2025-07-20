package com.SpringBoot.BillingService.Billing_service.Clients;

import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.BookingResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface BookingClient {
    @GetExchange("/bookings/{id}")
    BookingResponseDTO getById(@PathVariable("id") Long id);
}
