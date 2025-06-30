package com.SpringBoot.BookingService.Booking_service.DTO;

import jakarta.validation.constraints.NotNull;

public record CancelBookingRequestDTO(
        @NotNull Long bookingId
) {
}
