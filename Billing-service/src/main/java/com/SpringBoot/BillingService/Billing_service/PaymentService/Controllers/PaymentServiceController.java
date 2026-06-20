package com.SpringBoot.BillingService.Billing_service.PaymentService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing/payments")
@RequiredArgsConstructor
public class PaymentServiceController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> makePayment(@RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.makePayment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByInvoiceId(@PathVariable Long invoiceId) {
        PaymentResponseDTO response = paymentService.getPaymentByInvoiceId(invoiceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.refundPayment(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
