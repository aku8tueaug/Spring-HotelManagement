package com.SpringBoot.BookingService.Booking_service.DTO;

import java.math.BigDecimal;

public record InvoiceResponseDTO(
        Long invoiceId,
        Long bookingId,
        String invoiceNumber,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String status
) {
}
