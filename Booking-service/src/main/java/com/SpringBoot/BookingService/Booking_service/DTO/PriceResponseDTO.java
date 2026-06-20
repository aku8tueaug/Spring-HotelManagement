package com.SpringBoot.BookingService.Booking_service.DTO;

import java.math.BigDecimal;

public record PriceResponseDTO(
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal finalAmount
) {
}
