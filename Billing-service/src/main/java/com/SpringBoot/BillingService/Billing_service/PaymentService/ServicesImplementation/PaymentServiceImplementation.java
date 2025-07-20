package com.SpringBoot.BillingService.Billing_service.PaymentService.ServicesImplementation;


import com.SpringBoot.BillingService.Billing_service.Clients.BookingClient;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.Payment;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Repository.PaymentRepository;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.BookingResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.BookingStatus;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final PricingService pricingClient;

    public PaymentServiceImplementation(PaymentRepository paymentRepository,
                                        BookingClient bookingClient,
                                        PricingService pricingClient) {
        this.paymentRepository = paymentRepository;
        this.bookingClient = bookingClient;
        this.pricingClient = pricingClient;
    }

    @Override
    public PaymentResponseDTO makePayment(PaymentRequestDTO paymentRequest) {
        // Check booking exists and is confirmed
        BookingResponseDTO booking = bookingClient.getById(paymentRequest.bookingId());
        if (booking.bookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed bookings can be paid.");
        }

        // Check if already paid
        if (paymentRepository.findByBookingId(paymentRequest.bookingId()).isPresent()) {
            throw new IllegalArgumentException("Payment already completed for this booking.");
        }

//        PriceRequestDTO priceRequestDTO = mapToPriceRequestDTO(booking);
        // Get price
//        BigDecimal amount = pricingClient.getPriceForBooking(priceRequestDTO);
          BigDecimal amount = pricingClient.getPriceForBookingById(paymentRequest.bookingId());
        // Simulate payment
        Payment payment = Payment.builder()
                .bookingId(paymentRequest.bookingId())
                .paymentMethod(paymentRequest.paymentMethod())
                .amount(amount)
                .status(PaymentStatus.COMPLETED) // assume success
                .paymentDate(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        return mapToDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentDate()
        );
    }

}
