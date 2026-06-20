package com.SpringBoot.BillingService.Billing_service.InvoiceService.Controllers;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@RequestBody InvoiceRequestDTO request) {
        InvoiceResponseDTO response = invoiceService.createInvoice(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByBookingId(@PathVariable Long bookingId) {
        InvoiceResponseDTO response = invoiceService.getInvoiceByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/booking/{bookingId}/cancel")
    public ResponseEntity<InvoiceResponseDTO> cancelInvoice(@PathVariable Long bookingId) {
        InvoiceResponseDTO response = invoiceService.cancelInvoiceByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
}
