package com.SpringBoot.BillingService.Billing_service.InvoiceService.Services;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO request);
    InvoiceResponseDTO getInvoiceById(Long invoiceId);
    InvoiceResponseDTO getInvoiceByBookingId(Long bookingId);
    InvoiceResponseDTO cancelInvoiceByBookingId(Long bookingId);
}
