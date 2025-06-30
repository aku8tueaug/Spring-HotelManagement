package com.SpringBoot.InventoryService.Inventory_service.Configuration;

import com.SpringBoot.InventoryService.Inventory_service.Client.HotelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HotelClientConfiguration {
    @Bean
    public HotelClient hotelClient(WebClient.Builder builder, @Value("${spring.hotelService.url}") String baseUrl)
    {
        WebClientAdapter adapter = WebClientAdapter.create(builder.baseUrl(baseUrl).build());
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return httpServiceProxyFactory.createClient(HotelClient.class);
    }
}
