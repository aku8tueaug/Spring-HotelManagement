package com.SpringBoot.BillingService.Billing_service.PaymentService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Payment")
public class PaymentServiceController {
    private final PaymentService paymentService;

    public PaymentServiceController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> makePayment(@RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.makePayment(request));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByBookingId(@PathVariable Long bookingId) {
        PaymentResponseDTO response = paymentService.getPaymentByBookingId(bookingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
