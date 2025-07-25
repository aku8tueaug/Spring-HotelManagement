package com.SpringBoot.InventoryService.Inventory_service.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient.Builder webClientBuilder()
    {
        return WebClient.builder();
    }
}
