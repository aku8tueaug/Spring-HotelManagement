package com.SpringBoot.BillingService.Billing_service.PaymentService.Repository;

import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByInvoiceId(Long invoiceId);
    Optional<Payment> findByTransactionReference(String transactionReference);
}
