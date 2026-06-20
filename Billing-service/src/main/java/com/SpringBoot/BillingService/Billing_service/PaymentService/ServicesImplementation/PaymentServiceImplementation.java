package com.SpringBoot.BillingService.Billing_service.PaymentService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.Invoice;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository.InvoiceRepository;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.Payment;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Repository.PaymentRepository;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public PaymentResponseDTO makePayment(PaymentRequestDTO paymentRequest) {
        // Idempotency: Check if this transactionReference has already been processed
        Optional<Payment> existingPayment = paymentRepository.findByTransactionReference(paymentRequest.transactionReference());
        if (existingPayment.isPresent()) {
            return mapToDTO(existingPayment.get());
        }

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(paymentRequest.invoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for ID: " + paymentRequest.invoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Invoice is cancelled");
        }

        // Validate amount
        if (paymentRequest.amount().compareTo(invoice.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("Payment amount " + paymentRequest.amount() + 
                    " does not match invoice total amount " + invoice.getTotalAmount());
        }

        // Create payment
        Payment payment = Payment.builder()
                .invoiceId(paymentRequest.invoiceId())
                .amount(paymentRequest.amount())
                .paymentMethod(paymentRequest.paymentMethod())
                .transactionReference(paymentRequest.transactionReference())
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Update Invoice status
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        return mapToDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for ID: " + paymentId));
        return mapToDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByInvoiceId(Long invoiceId) {
        Payment payment = paymentRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for invoice ID: " + invoiceId));
        return mapToDTO(payment);
    }

    @Override
    public PaymentResponseDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        // Mark payment as refunded
        final Long invoiceId = payment.getInvoiceId();
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        // Mark invoice as refunded
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for ID: " + invoiceId));
        invoice.setStatus(InvoiceStatus.REFUNDED);
        invoiceRepository.save(invoice);

        return mapToDTO(savedPayment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getInvoiceId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionReference(),
                payment.getStatus(),
                payment.getPaymentDate()
        );
    }
}
