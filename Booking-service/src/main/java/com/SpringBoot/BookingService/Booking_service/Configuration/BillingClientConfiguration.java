package com.SpringBoot.BookingService.Booking_service.Configuration;

import com.SpringBoot.BookingService.Booking_service.Client.HotelClient;
import com.SpringBoot.BookingService.Booking_service.Client.PricingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BillingClientConfiguration {
    @Bean
    public PricingClient pricingClient(WebClient.Builder builder, @Value("${spring.BillingService.url}") String baseUrl)
    {
        WebClientAdapter adapter = WebClientAdapter.create(builder.baseUrl(baseUrl).build());
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return httpServiceProxyFactory.createClient(PricingClient.class);
    }
}
