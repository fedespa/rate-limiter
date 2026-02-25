package com.app.rate_limiter.identity.auth.api;

import com.app.rate_limiter.identity.auth.api.response.LoginResponse;
import com.app.rate_limiter.identity.auth.api.response.RegisterResponse;
import com.app.rate_limiter.identity.auth.request.LoginRequest;
import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().build();
    }

}
