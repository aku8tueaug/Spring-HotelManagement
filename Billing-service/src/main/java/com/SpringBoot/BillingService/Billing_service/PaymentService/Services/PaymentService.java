package com.SpringBoot.BillingService.Billing_service.PaymentService.Services;

import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO makePayment(PaymentRequestDTO paymentRequest);
    PaymentResponseDTO getPaymentById(Long paymentId);
    PaymentResponseDTO getPaymentByInvoiceId(Long invoiceId);
    PaymentResponseDTO refundPayment(Long paymentId);
}
