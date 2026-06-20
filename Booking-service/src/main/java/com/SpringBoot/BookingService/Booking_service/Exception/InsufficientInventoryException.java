package com.SpringBoot.BookingService.Booking_service.Exception;

public class InsufficientInventoryException extends RuntimeException{

    public InsufficientInventoryException(String msg)
    {
        super(msg);
    }
}
