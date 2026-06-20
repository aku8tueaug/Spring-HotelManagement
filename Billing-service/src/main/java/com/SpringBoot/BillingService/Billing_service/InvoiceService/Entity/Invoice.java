package com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"invoiceNumber"}),
    @UniqueConstraint(columnNames = {"bookingId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal taxAmount;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Version
    private Long version;
}
