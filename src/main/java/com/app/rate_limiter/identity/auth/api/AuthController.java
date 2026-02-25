package com.app.rate_limiter.identity.auth.api;

import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        this.authService.register(request);
    }

}
