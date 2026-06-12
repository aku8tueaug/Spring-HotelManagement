package com.SpringBoot.BookingService.Booking_service.Configuration;

import com.SpringBoot.BookingService.Booking_service.Client.InventoryClient;
import com.SpringBoot.BookingService.Booking_service.Client.RoomClient;
import com.SpringBoot.BookingService.Booking_service.Security.Jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class RoomClientConfiguration {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public RoomClient roomClient(WebClient.Builder builder, @Value("${spring.roomService.url}") String baseUrl) {
        WebClient webClient = builder.clone()
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    String authHeader = jwtTokenProvider.getAuthorizationHeader();

                    ClientRequest filteredRequest =  ClientRequest.from(request)
                            .headers(headers ->{
                                if(authHeader !=null)
                                    headers.set("Authorization", authHeader);
                            }).build();
                    return next.exchange(filteredRequest);
                })
                .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(RoomClient.class);
    }
}
