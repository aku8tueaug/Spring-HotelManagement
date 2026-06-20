package com.SpringBoot.BookingService.Booking_service.Exception;

public class BookingCreationFailedException extends Exception {
    public BookingCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
