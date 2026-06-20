package com.SpringBoot.BillingService.Billing_service.InvoiceService.ServicesImplementation;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.Invoice;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository.InvoiceRepository;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImplementation implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request) {
        // Idempotency check: return existing if present
        Optional<Invoice> existingInvoice = invoiceRepository.findByBookingId(request.bookingId());
        if (existingInvoice.isPresent()) {
            return mapToResponseDTO(existingInvoice.get());
        }

        String invoiceNumber = "INV-" + request.bookingId() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Invoice invoice = Invoice.builder()
                .bookingId(request.bookingId())
                .invoiceNumber(invoiceNumber)
                .subtotal(request.subtotal())
                .taxAmount(request.taxAmount())
                .totalAmount(request.totalAmount())
                .status(InvoiceStatus.ISSUED)
                .build();

        invoice = invoiceRepository.save(invoice);
        return mapToResponseDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for ID: " + invoiceId));
        return mapToResponseDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByBookingId(Long bookingId) {
        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for booking ID: " + bookingId));
        return mapToResponseDTO(invoice);
    }

    @Override
    public InvoiceResponseDTO cancelInvoiceByBookingId(Long bookingId) {
        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for booking ID: " + bookingId));
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice = invoiceRepository.save(invoice);
        return mapToResponseDTO(invoice);
    }

    private InvoiceResponseDTO mapToResponseDTO(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getInvoiceId(),
                invoice.getBookingId(),
                invoice.getInvoiceNumber(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getStatus()
        );
    }
}
