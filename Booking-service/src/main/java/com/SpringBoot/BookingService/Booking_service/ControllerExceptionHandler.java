package com.SpringBoot.BookingService.Booking_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse notFound(NoSuchElementException ex)
    {
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Validation failed")
                .detail(String.join("; ", errors))
                .build();
    }

//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }
}
