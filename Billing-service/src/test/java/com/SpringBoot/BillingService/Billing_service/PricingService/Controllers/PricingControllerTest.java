package com.SpringBoot.BillingService.Billing_service.PricingService.Controllers;

import com.SpringBoot.BillingService.Billing_service.PricingService.DTO.*;
import com.SpringBoot.BillingService.Billing_service.PricingService.Entity.RoomType;
import com.SpringBoot.BillingService.Billing_service.PricingService.Services.PricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingController.class)
public class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingService pricingService;

    @Test
    void testCalculatePrice_Success() throws Exception {
        PriceRequestDTO request = new PriceRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(1), 2);
        PriceResponseDTO response = new PriceResponseDTO(BigDecimal.valueOf(500), BigDecimal.valueOf(60), BigDecimal.valueOf(560));

        when(pricingService.calculateDynamicPrice(any(PriceRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalAmount").value(560));

        verify(pricingService).calculateDynamicPrice(any(PriceRequestDTO.class));
    }

    @Test
    void testGetPriceByBookingId_Success() throws Exception {
        when(pricingService.getPriceForBookingById(10L)).thenReturn(BigDecimal.valueOf(560));

        mockMvc.perform(get("/billing/pricing/amount/booking/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(560));

        verify(pricingService).getPriceForBookingById(10L);
    }

    @Test
    void testAddRatePlan_Success() throws Exception {
        RatePlanRequestDTO request = new RatePlanRequestDTO(1L, RoomType.STANDARD, BigDecimal.valueOf(500), LocalDate.now(), LocalDate.now().plusDays(10), true);
        RatePlanResponseDTO response = new RatePlanResponseDTO(101L, 1L, RoomType.STANDARD, BigDecimal.valueOf(500), LocalDate.now(), LocalDate.now().plusDays(10), true);

        when(pricingService.addRatePlan(any(RatePlanRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing/rate-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ratePlanId").value(101));

        verify(pricingService).addRatePlan(any(RatePlanRequestDTO.class));
    }

    @Test
    void testUpdateRatePlan_Success() throws Exception {
        RatePlanRequestDTO request = new RatePlanRequestDTO(1L, RoomType.STANDARD, BigDecimal.valueOf(550), LocalDate.now(), LocalDate.now().plusDays(10), true);
        RatePlanResponseDTO response = new RatePlanResponseDTO(101L, 1L, RoomType.STANDARD, BigDecimal.valueOf(550), LocalDate.now(), LocalDate.now().plusDays(10), true);

        when(pricingService.updateRatePlan(eq(101L), any(RatePlanRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/billing/rate-plans/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(550));

        verify(pricingService).updateRatePlan(eq(101L), any(RatePlanRequestDTO.class));
    }

    @Test
    void testDeleteRatePlan_Success() throws Exception {
        mockMvc.perform(delete("/billing/rate-plans/101"))
                .andExpect(status().isNoContent());

        verify(pricingService).deleteRatePlan(101L);
    }

    @Test
    void testAddSeasonalPricing_Success() throws Exception {
        SeasonalPricingRequestDTO request = new SeasonalPricingRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(5), "MULTIPLIER", BigDecimal.valueOf(1.5));
        SeasonalPricingResponseDTO response = new SeasonalPricingResponseDTO(201L, 1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(5), "MULTIPLIER", BigDecimal.valueOf(1.5));

        when(pricingService.addSeasonalPricing(any(SeasonalPricingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/billing/seasonal-pricings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(201));

        verify(pricingService).addSeasonalPricing(any(SeasonalPricingRequestDTO.class));
    }

    @Test
    void testUpdateSeasonalPricing_Success() throws Exception {
        SeasonalPricingRequestDTO request = new SeasonalPricingRequestDTO(1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(5), "FLAT_ADD", BigDecimal.valueOf(100));
        SeasonalPricingResponseDTO response = new SeasonalPricingResponseDTO(201L, 1L, RoomType.STANDARD, LocalDate.now(), LocalDate.now().plusDays(5), "FLAT_ADD", BigDecimal.valueOf(100));

        when(pricingService.updateSeasonalPricing(eq(201L), any(SeasonalPricingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/billing/seasonal-pricings/201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adjustmentType").value("FLAT_ADD"));

        verify(pricingService).updateSeasonalPricing(eq(201L), any(SeasonalPricingRequestDTO.class));
    }

    @Test
    void testDeleteSeasonalPricing_Success() throws Exception {
        mockMvc.perform(delete("/billing/seasonal-pricings/201"))
                .andExpect(status().isNoContent());

        verify(pricingService).deleteSeasonalPricing(201L);
    }
}
