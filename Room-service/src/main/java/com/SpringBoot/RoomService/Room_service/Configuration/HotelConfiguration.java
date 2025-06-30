package com.SpringBoot.RoomService.Room_service.Configuration;

import com.SpringBoot.RoomService.Room_service.HTTPClient.HotelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HotelConfiguration {

    @Bean
    public WebClient webClient(@Value("${spring.hotelService.url}") String baseUrl)
    {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
    @Bean
    public HotelClient hotelClient(WebClient webClient) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(HotelClient.class);
    }
}
