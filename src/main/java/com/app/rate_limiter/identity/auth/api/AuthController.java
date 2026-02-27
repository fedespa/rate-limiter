package com.app.rate_limiter.identity.auth.api;

import com.app.rate_limiter.identity.auth.api.response.AccessTokenResponse;
import com.app.rate_limiter.identity.auth.api.response.RegisterResponse;
import com.app.rate_limiter.identity.auth.api.response.TokensResponse;
import com.app.rate_limiter.identity.auth.request.LoginRequest;
import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = this.authService.register(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("token") String token) {

        this.authService.verify(token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        TokensResponse tokensResponse = this.authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokensResponse.refreshToken())
                .httpOnly(true)
                .secure(false) // TRUE EN PROD
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                new AccessTokenResponse(tokensResponse.accessToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ){

        TokensResponse tokensResponse = this.authService.refreshToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokensResponse.refreshToken())
                .httpOnly(true)
                .secure(false) // TRUE EN PROD
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new AccessTokenResponse(tokensResponse.accessToken()));
    }


}
