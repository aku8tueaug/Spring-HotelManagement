package com.SpringBoot.InventoryService.Inventory_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<String> handleNotFound(WebClientResponseException.NotFound ex) {
        String uri = ex.getRequest() != null ? ex.getRequest().getURI().toString() : "unknown";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found in external service: " + uri);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClient(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body("Downstream service error: " + ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex.getMessage());
    }


}
