package com.SpringBoot.BookingService.Booking_service.Configuration;

import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.DTO.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class InventoryClientConfiguration {
    @Bean
    public InventoryClient inventoryClient(WebClient.Builder builder, @Value("${spring.inventoryService.url}") String baseUrl)
    {
        WebClientAdapter adapter = WebClientAdapter.create(builder.baseUrl(baseUrl).build());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(InventoryClient.class);
    }
}
