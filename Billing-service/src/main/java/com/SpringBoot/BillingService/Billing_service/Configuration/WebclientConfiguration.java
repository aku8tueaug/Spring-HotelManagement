package com.SpringBoot.BillingService.Billing_service.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfiguration {
    @Bean
    public WebClient.Builder webClient()
    {
        return WebClient.builder();
    }
}
