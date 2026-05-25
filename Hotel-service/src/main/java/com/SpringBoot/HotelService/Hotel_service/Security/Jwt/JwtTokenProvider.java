package com.SpringBoot.HotelService.Hotel_service.Security.Jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final HttpServletRequest request;

    public String getAuthorizationHeader()
    {
        return request.getHeader("Authorization");
    }
}
