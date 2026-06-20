package com.SpringBoot.BookingService.Booking_service.Client;

import com.SpringBoot.BookingService.Booking_service.DTO.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface BillingClient {

    @PostExchange("/billing/pricing/calculate")
    PriceResponseDTO calculatePrice(@RequestBody PriceRequestDTO request);

    @PostExchange("/billing/invoices")
    InvoiceResponseDTO createInvoice(@RequestBody InvoiceRequestDTO request);

    @PostExchange("/billing/invoices/booking/{bookingId}/cancel")
    InvoiceResponseDTO cancelInvoice(@PathVariable("bookingId") Long bookingId);
}
