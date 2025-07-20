package com.SpringBoot.BillingService.Billing_service.Configuration;


import com.SpringBoot.BillingService.Billing_service.Clients.BookingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BookingClientConfiguration {
    @Bean
    public BookingClient bookingClient(WebClient.Builder builder, @Value("${spring.bookingService.url}") String baseUrl)
    {
        WebClientAdapter adapter = WebClientAdapter.create(builder.baseUrl(baseUrl).build());
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return httpServiceProxyFactory.createClient(BookingClient.class);
    }
}
