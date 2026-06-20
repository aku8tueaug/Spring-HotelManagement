package com.SpringBoot.BillingService.Billing_service.PaymentService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentRequestDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.DTO.PaymentResponseDTO;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Entity.PaymentStatus;
import com.SpringBoot.BillingService.Billing_service.PaymentService.Services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentServiceController.class)
public class PaymentServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void testMakePayment_Success() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO(1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123");
        PaymentResponseDTO response = new PaymentResponseDTO(55L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123", PaymentStatus.COMPLETED, LocalDateTime.now());

        when(paymentService.makePayment(any(PaymentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(55))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(paymentService).makePayment(any(PaymentRequestDTO.class));
    }

    @Test
    void testGetPaymentById_Success() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO(55L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123", PaymentStatus.COMPLETED, LocalDateTime.now());

        when(paymentService.getPaymentById(55L)).thenReturn(response);

        mockMvc.perform(get("/billing/payments/55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(55));

        verify(paymentService).getPaymentById(55L);
    }

    @Test
    void testGetPaymentByInvoiceId_Success() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO(55L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123", PaymentStatus.COMPLETED, LocalDateTime.now());

        when(paymentService.getPaymentByInvoiceId(1L)).thenReturn(response);

        mockMvc.perform(get("/billing/payments/invoice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(1));

        verify(paymentService).getPaymentByInvoiceId(1L);
    }

    @Test
    void testRefundPayment_Success() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO(55L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD", "TX-123", PaymentStatus.REFUNDED, LocalDateTime.now());

        when(paymentService.refundPayment(55L)).thenReturn(response);

        mockMvc.perform(post("/billing/payments/55/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService).refundPayment(55L);
    }
}
