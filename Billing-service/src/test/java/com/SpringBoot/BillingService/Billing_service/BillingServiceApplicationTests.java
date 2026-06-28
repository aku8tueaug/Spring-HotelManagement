package com.SpringBoot.BillingService.Billing_service;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Repository.InvoiceRepository;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Services.InvoiceService;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Repository.PaymentRepository;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.PriceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.AdjustmentType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RatePlan;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.SeasonalPricing;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.RatePlanRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Repository.SeasonalPricingRepository;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BillingServiceApplicationTests {

	@Autowired
	private PricingService pricingService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RatePlanRepository ratePlanRepository;

	@Autowired
	private SeasonalPricingRepository seasonalPricingRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@BeforeEach
	void setUp() {
		paymentRepository.deleteAll();
		invoiceRepository.deleteAll();
		seasonalPricingRepository.deleteAll();
		ratePlanRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testCalculatePrice_BaseRate() {
		// Arrange: Active rate plan of 200 per night for 2026-06-20 to 2026-06-25
		RatePlan ratePlan = RatePlan.builder()
				.hotelId(1L)
				.roomType(RoomType.STANDARD)
				.basePrice(BigDecimal.valueOf(200.00))
				.startDate(LocalDate.of(2026, 6, 20))
				.endDate(LocalDate.of(2026, 6, 25))
				.active(true)
				.build();
		ratePlanRepository.save(ratePlan);

		// Act: Stay length of 2 nights
		PriceRequestDTO request = new PriceRequestDTO(
				1L,
				RoomType.STANDARD,
				LocalDate.of(2026, 6, 20),
				LocalDate.of(2026, 6, 22),
				2 // 2 guests
		);

		PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

		// Assert: 2 nights * 200 = 400. Tax is 12% because 400 < 1000 -> 48.00. Final amount = 448.00
		assertEquals(0, BigDecimal.valueOf(400.00).compareTo(response.subtotal()));
		assertEquals(0, BigDecimal.valueOf(48.00).compareTo(response.taxAmount()));
		assertEquals(0, BigDecimal.valueOf(448.00).compareTo(response.finalAmount()));
	}

	@Test
	void testCalculatePrice_SeasonalAdjustmentMultiplier() {
		// Arrange
		RatePlan ratePlan = RatePlan.builder()
				.hotelId(1L)
				.roomType(RoomType.STANDARD)
				.basePrice(BigDecimal.valueOf(200.00))
				.startDate(LocalDate.of(2026, 6, 20))
				.endDate(LocalDate.of(2026, 6, 25))
				.active(true)
				.build();
		ratePlanRepository.save(ratePlan);

		// Seasonal pricing multiplier of 1.50
		SeasonalPricing seasonalPricing = SeasonalPricing.builder()
				.hotelId(1L)
				.roomType(RoomType.STANDARD)
				.startDate(LocalDate.of(2026, 6, 20))
				.endDate(LocalDate.of(2026, 6, 25))
				.adjustmentType(AdjustmentType.MULTIPLIER)
				.adjustmentValue(BigDecimal.valueOf(1.50))
				.build();
		seasonalPricingRepository.save(seasonalPricing);

		// Act
		PriceRequestDTO request = new PriceRequestDTO(
				1L,
				RoomType.STANDARD,
				LocalDate.of(2026, 6, 20),
				LocalDate.of(2026, 6, 22),
				2
		);

		PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

		// Assert: 2 nights * (200 * 1.50) = 600. Tax is 12% -> 72.00. Final = 672.00
		assertEquals(0, BigDecimal.valueOf(600.00).compareTo(response.subtotal()));
		assertEquals(0, BigDecimal.valueOf(72.00).compareTo(response.taxAmount()));
		assertEquals(0, BigDecimal.valueOf(672.00).compareTo(response.finalAmount()));
	}

	@Test
	void testCalculatePrice_HighTaxBracket() {
		// Arrange: Base rate of 600.00 per night
		RatePlan ratePlan = RatePlan.builder()
				.hotelId(1L)
				.roomType(RoomType.DELUXE)
				.basePrice(BigDecimal.valueOf(600.00))
				.startDate(LocalDate.of(2026, 6, 20))
				.endDate(LocalDate.of(2026, 6, 25))
				.active(true)
				.build();
		ratePlanRepository.save(ratePlan);

		// Act: 2 nights
		PriceRequestDTO request = new PriceRequestDTO(
				1L,
				RoomType.DELUXE,
				LocalDate.of(2026, 6, 20),
				LocalDate.of(2026, 6, 22),
				2
		);

		PriceResponseDTO response = pricingService.calculateDynamicPrice(request);

		// Assert: 2 nights * 600 = 1200. Subtotal >= 1000, so tax rate is 18% -> 216.00. Final = 1416.00
		assertEquals(0, BigDecimal.valueOf(1200.00).compareTo(response.subtotal()));
		assertEquals(0, BigDecimal.valueOf(216.00).compareTo(response.taxAmount()));
		assertEquals(0, BigDecimal.valueOf(1416.00).compareTo(response.finalAmount()));
	}

	@Test
	void testInvoiceIdempotency() {
		InvoiceRequestDTO request = new InvoiceRequestDTO(
				999L,
				BigDecimal.valueOf(200.00),
				BigDecimal.valueOf(24.00),
				BigDecimal.valueOf(224.00)
		);

		// Create invoice first time
		InvoiceResponseDTO response1 = invoiceService.createInvoice(request);
		assertNotNull(response1.invoiceId());
		assertEquals(InvoiceStatus.ISSUED, response1.status());

		// Call createInvoice second time for same bookingId
		InvoiceResponseDTO response2 = invoiceService.createInvoice(request);

		// Assert they are identical
		assertEquals(response1.invoiceId(), response2.invoiceId());
		assertEquals(response1.invoiceNumber(), response2.invoiceNumber());
	}

	@Test
	void testPaymentIdempotency() {
		// Arrange: Create an invoice
		InvoiceRequestDTO invoiceRequest = new InvoiceRequestDTO(
				888L,
				BigDecimal.valueOf(100.00),
				BigDecimal.valueOf(12.00),
				BigDecimal.valueOf(112.00)
		);
		InvoiceResponseDTO invoice = invoiceService.createInvoice(invoiceRequest);

		String transactionRef = UUID.randomUUID().toString();
		PaymentRequestDTO paymentRequest = new PaymentRequestDTO(
				invoice.invoiceId(),
				BigDecimal.valueOf(112.00),
				"CREDIT_CARD",
				transactionRef
		);

		// Act: First payment
		PaymentResponseDTO payment1 = paymentService.makePayment(paymentRequest);
		assertNotNull(payment1.paymentId());
		assertEquals(PaymentStatus.COMPLETED, payment1.status());

		// Second payment with same transactionReference
		PaymentResponseDTO payment2 = paymentService.makePayment(paymentRequest);

		// Assert they are identical
		assertEquals(payment1.paymentId(), payment2.paymentId());
		assertEquals(payment1.transactionReference(), payment2.transactionReference());
	}
}
