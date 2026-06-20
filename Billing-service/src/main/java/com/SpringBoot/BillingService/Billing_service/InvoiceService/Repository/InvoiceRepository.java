package com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingId(Long bookingId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
