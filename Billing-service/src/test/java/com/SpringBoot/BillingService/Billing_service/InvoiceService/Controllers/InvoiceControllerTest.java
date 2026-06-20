package com.SpringBoot.BillingService.Billing_service.InvoiceService.Controllers;

import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceRequestDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.DTO.InvoiceResponseDTO;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Entity.InvoiceStatus;
import com.SpringBoot.BillingService.Billing_service.InvoiceService.Services.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InvoiceService invoiceService;

    @Test
    void testCreateInvoice_Success() throws Exception {
        InvoiceRequestDTO request = new InvoiceRequestDTO(10L, BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180));
        InvoiceResponseDTO response = new InvoiceResponseDTO(123L, 10L, "INV-10-123", BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180), InvoiceStatus.ISSUED);

        when(invoiceService.createInvoice(any(InvoiceRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceId").value(123))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-10-123"));

        verify(invoiceService).createInvoice(any(InvoiceRequestDTO.class));
    }

    @Test
    void testGetInvoiceById_Success() throws Exception {
        InvoiceResponseDTO response = new InvoiceResponseDTO(123L, 10L, "INV-10-123", BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180), InvoiceStatus.ISSUED);

        when(invoiceService.getInvoiceById(123L)).thenReturn(response);

        mockMvc.perform(get("/billing/invoices/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(123))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-10-123"));

        verify(invoiceService).getInvoiceById(123L);
    }

    @Test
    void testGetInvoiceByBookingId_Success() throws Exception {
        InvoiceResponseDTO response = new InvoiceResponseDTO(123L, 10L, "INV-10-123", BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180), InvoiceStatus.ISSUED);

        when(invoiceService.getInvoiceByBookingId(10L)).thenReturn(response);

        mockMvc.perform(get("/billing/invoices/booking/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-10-123"));

        verify(invoiceService).getInvoiceByBookingId(10L);
    }

    @Test
    void testCancelInvoice_Success() throws Exception {
        InvoiceResponseDTO response = new InvoiceResponseDTO(123L, 10L, "INV-10-123", BigDecimal.valueOf(1000), BigDecimal.valueOf(180), BigDecimal.valueOf(1180), InvoiceStatus.CANCELLED);

        when(invoiceService.cancelInvoiceByBookingId(10L)).thenReturn(response);

        mockMvc.perform(post("/billing/invoices/booking/10/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(invoiceService).cancelInvoiceByBookingId(10L);
    }
}
