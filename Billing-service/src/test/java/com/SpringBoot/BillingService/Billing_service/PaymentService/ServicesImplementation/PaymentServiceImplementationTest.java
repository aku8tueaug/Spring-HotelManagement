package com.SpringBoot.BillingService.Billing_service.PaymentService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.Invoice;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository.InvoiceRepository;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.Payment;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplementationTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentServiceImplementation paymentService;

    private Payment createSamplePayment(Long paymentId, Long invoiceId, BigDecimal amount, String ref, PaymentStatus status) {
        return Payment.builder()
                .paymentId(paymentId)
                .invoiceId(invoiceId)
                .amount(amount)
                .paymentMethod("CREDIT_CARD")
                .transactionReference(ref)
                .status(status)
                .paymentDate(LocalDateTime.now())
                .build();
    }

    private Invoice createSampleInvoice(Long id, InvoiceStatus status, BigDecimal total) {
        return Invoice.builder()
                .invoiceId(id)
                .bookingId(10L)
                .invoiceNumber("INV-123")
                .subtotal(total)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(total)
                .status(status)
                .build();
    }

    @Test
    void testMakePayment_Idempotency_Success() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");
        Payment existing = createSamplePayment(55L, 1L, BigDecimal.valueOf(100), "TX-123", PaymentStatus.COMPLETED);

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.of(existing));

        PaymentResponseDTO response = paymentService.makePayment(request);

        assertNotNull(response);
        assertEquals(55L, response.paymentId());
        verify(invoiceRepository, never()).findById(any());
    }

    @Test
    void testMakePayment_InvoiceNotFound_ThrowsIllegalArgumentException() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.empty());
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.makePayment(request);
        });
        assertEquals("Invoice not found for ID: 1", ex.getMessage());
    }

    @Test
    void testMakePayment_InvoiceAlreadyPaid_ThrowsIllegalStateException() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");
        Invoice invoice = createSampleInvoice(1L, InvoiceStatus.PAID, BigDecimal.valueOf(100));

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.empty());
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.makePayment(request);
        });
        assertEquals("Invoice is already paid", ex.getMessage());
    }

    @Test
    void testMakePayment_InvoiceCancelled_ThrowsIllegalStateException() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");
        Invoice invoice = createSampleInvoice(1L, InvoiceStatus.CANCELLED, BigDecimal.valueOf(100));

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.empty());
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.makePayment(request);
        });
        assertEquals("Invoice is cancelled", ex.getMessage());
    }

    @Test
    void testMakePayment_AmountMismatch_ThrowsIllegalArgumentException() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(150), "CREDIT_CARD", "TX-123");
        Invoice invoice = createSampleInvoice(1L, InvoiceStatus.ISSUED, BigDecimal.valueOf(100));

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.empty());
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.makePayment(request);
        });
        assertTrue(ex.getMessage().contains("does not match invoice total amount"));
    }

    @Test
    void testMakePayment_Success() {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");
        Invoice invoice = createSampleInvoice(1L, InvoiceStatus.ISSUED, BigDecimal.valueOf(100));
        Payment saved = createSamplePayment(55L, 1L, BigDecimal.valueOf(100), "TX-123", PaymentStatus.COMPLETED);

        when(paymentRepository.findByTransactionReference("TX-123")).thenReturn(Optional.empty());
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponseDTO response = paymentService.makePayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.COMPLETED, response.status());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        verify(invoiceRepository).save(invoice);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testGetPaymentById_Success() {
        Payment payment = createSamplePayment(55L, 1L, BigDecimal.valueOf(100), "TX-123", PaymentStatus.COMPLETED);
        when(paymentRepository.findById(55L)).thenReturn(Optional.of(payment));

        PaymentResponseDTO response = paymentService.getPaymentById(55L);

        assertNotNull(response);
        assertEquals(55L, response.paymentId());
    }

    @Test
    void testGetPaymentById_NotFound_ThrowsIllegalArgumentException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.getPaymentById(999L);
        });
        assertEquals("Payment not found for ID: 999", ex.getMessage());
    }

    @Test
    void testRefundPayment_NotCompleted_ThrowsIllegalStateException() {
        Payment payment = createSamplePayment(55L, 1L, BigDecimal.valueOf(100), "TX-123", PaymentStatus.REFUNDED);
        when(paymentRepository.findById(55L)).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            paymentService.refundPayment(55L);
        });
        assertEquals("Only completed payments can be refunded", ex.getMessage());
    }

    @Test
    void testRefundPayment_Success() {
        Payment payment = createSamplePayment(55L, 1L, BigDecimal.valueOf(100), "TX-123", PaymentStatus.COMPLETED);
        Invoice invoice = createSampleInvoice(1L, InvoiceStatus.PAID, BigDecimal.valueOf(100));

        when(paymentRepository.findById(55L)).thenReturn(Optional.of(payment));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(p -> p.getArgument(0));

        PaymentResponseDTO response = paymentService.refundPayment(55L);

        assertNotNull(response);
        assertEquals(PaymentStatus.REFUNDED, response.status());
        assertEquals(InvoiceStatus.REFUNDED, invoice.getStatus());
        verify(invoiceRepository).save(invoice);
        verify(paymentRepository).save(payment);
    }
}
