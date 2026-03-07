package com.app.rate_limiter.common.security.filter;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.common.util.jwt.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/v1/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        String email = jwtUtils.extractEmail(decodedJWT);
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UUID userId = UUID.fromString(decodedJWT.getSubject());
            boolean verified = decodedJWT.getClaim("verified").asBoolean();
            UUID tenantId = UUID.fromString(decodedJWT.getClaim("tenantId").asString());
            boolean deleted = decodedJWT.getClaim("deleted").asBoolean();

            CustomUserDetails userDetails = new CustomUserDetails(
                    userId,
                    email,
                    verified,
                    authorities,
                    tenantId,
                    deleted
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}