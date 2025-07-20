package com.SpringBoot.BookingService.Booking_service.Client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.math.BigDecimal;

@HttpExchange
public interface PricingClient {
    @GetExchange("/Pricing/amount/booking/{id}")
    public BigDecimal getPriceByBookingId(@PathVariable("id") Long id);
}
