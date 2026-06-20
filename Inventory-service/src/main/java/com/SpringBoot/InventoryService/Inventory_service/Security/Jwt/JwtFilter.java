package com.SpringBoot.InventoryService.Inventory_service.Security.Jwt;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;

        // 🔍 Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
        }

        // ✅ Validate and authenticate
        if ( username != null
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtService.isTokenValid(jwt) ) {

            // Extract Roles
            List<String> roles = jwtService.extractRoles(jwt);

            List<GrantedAuthority> authorities =
                    roles.stream()
                            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                            .toList();

            UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🔥 Set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}
