package com.SpringBoot.BillingService.Billing_service.InvoiceService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.Invoice;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceImplementationTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImplementation invoiceService;

    private Invoice createSampleInvoice(Long id, Long bookingId, String number, InvoiceStatus status) {
        return Invoice.builder()
                .invoiceId(id)
                .bookingId(bookingId)
                .invoiceNumber(number)
                .subtotal(BigDecimal.valueOf(1000))
                .taxAmount(BigDecimal.valueOf(180))
                .totalAmount(BigDecimal.valueOf(1180))
                .status(status)
                .build();
    }

    @Test
    void testCreateInvoice_Idempotency_Success() {
        InvoiceRequestDTO request = new InvoiceRequestDTO(10L, BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180));
        Invoice existing = createSampleInvoice(123L, 10L, "INV-10-OLD", InvoiceStatus.ISSUED);

        when(invoiceRepository.findByBookingId(10L)).thenReturn(Optional.of(existing));

        InvoiceResponseDTO response = invoiceService.createInvoice(request);

        assertNotNull(response);
        assertEquals(123L, response.invoiceId());
        assertEquals("INV-10-OLD", response.invoiceNumber());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void testCreateInvoice_New_Success() {
        InvoiceRequestDTO request = new InvoiceRequestDTO(10L, BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180));
        Invoice saved = createSampleInvoice(124L, 10L, "INV-10-NEW", InvoiceStatus.ISSUED);

        when(invoiceRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(saved);

        InvoiceResponseDTO response = invoiceService.createInvoice(request);

        assertNotNull(response);
        assertEquals(124L, response.invoiceId());
        assertEquals("INV-10-NEW", response.invoiceNumber());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testGetInvoiceById_Success() {
        Invoice invoice = createSampleInvoice(123L, 10L, "INV-123", InvoiceStatus.ISSUED);
        when(invoiceRepository.findById(123L)).thenReturn(Optional.of(invoice));

        InvoiceResponseDTO response = invoiceService.getInvoiceById(123L);

        assertNotNull(response);
        assertEquals(123L, response.invoiceId());
        assertEquals(InvoiceStatus.ISSUED, response.status());
    }

    @Test
    void testGetInvoiceById_NotFound_ThrowsIllegalArgumentException() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.getInvoiceById(999L);
        });
        assertEquals("Invoice not found for ID: 999", ex.getMessage());
    }

    @Test
    void testGetInvoiceByBookingId_Success() {
        Invoice invoice = createSampleInvoice(123L, 10L, "INV-123", InvoiceStatus.ISSUED);
        when(invoiceRepository.findByBookingId(10L)).thenReturn(Optional.of(invoice));

        InvoiceResponseDTO response = invoiceService.getInvoiceByBookingId(10L);

        assertNotNull(response);
        assertEquals(10L, response.bookingId());
    }

    @Test
    void testGetInvoiceByBookingId_NotFound_ThrowsIllegalArgumentException() {
        when(invoiceRepository.findByBookingId(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.getInvoiceByBookingId(999L);
        });
        assertEquals("Invoice not found for booking ID: 999", ex.getMessage());
    }

    @Test
    void testCancelInvoiceByBookingId_Success() {
        Invoice invoice = createSampleInvoice(123L, 10L, "INV-123", InvoiceStatus.ISSUED);
        when(invoiceRepository.findByBookingId(10L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        InvoiceResponseDTO response = invoiceService.cancelInvoiceByBookingId(10L);

        assertNotNull(response);
        assertEquals(InvoiceStatus.CANCELLED, response.status());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void testCancelInvoiceByBookingId_NotFound_ThrowsIllegalArgumentException() {
        when(invoiceRepository.findByBookingId(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.cancelInvoiceByBookingId(999L);
        });
        assertEquals("Invoice not found for booking ID: 999", ex.getMessage());
    }
}
