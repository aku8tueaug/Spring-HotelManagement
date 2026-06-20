package com.SpringBoot.BookingService.Booking_service.DTO;

import java.math.BigDecimal;

public record InvoiceRequestDTO(
        Long bookingId,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount
) {
}
