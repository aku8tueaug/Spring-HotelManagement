package com.SpringBoot.InventoryService.Inventory_service.Configuration;

import com.SpringBoot.InventoryService.Inventory_service.Client.RoomClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.security.PublicKey;

@Configuration
public class RoomClientConfiguration {
    @Bean
    public RoomClient roomClient(WebClient.Builder builder, @Value("${spring.roomService.url}") String baseUrl)
    {
        WebClientAdapter adapter = WebClientAdapter.create(builder.baseUrl(baseUrl).build());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(RoomClient.class);
    }

}
