package com.app.rate_limiter.core.ratelimit.api;

import com.app.rate_limiter.core.ratelimit.api.response.RateLimitResult;
import com.app.rate_limiter.core.ratelimit.model.CheckResponse;
import com.app.rate_limiter.core.ratelimit.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/check")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;

    @PostMapping
    public ResponseEntity<CheckResponse> checkLimit(@RequestHeader("X-API-KEY") String apiKey){

        RateLimitResult result = this.rateLimitService.check(apiKey);

        HttpHeaders headers = new HttpHeaders();

        // Burst
        headers.add("X-RateLimit-Limit", String.valueOf(result.config().burstCapacity()));

        // Cuantos quedan despues de esta peticion
        headers.add("X-RateLimit-Remaining", String.valueOf(result.remainingTokens()));

        // Tasa de recarga
        headers.add("X-RateLimit-Replenish-Rate", String.valueOf(result.config().refillRate()));

        if (!result.allowed()) {
            headers.add("Retry-After", "5");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .headers(headers)
                    .body(result.toResponse());
        }

        return ResponseEntity.ok().headers(headers).body(result.toResponse());

    }

}
