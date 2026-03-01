package com.app.rate_limiter.core.ratelimit.model;

public record CheckResponse(

        boolean allowed,
        int remainingTokens,
        long retryAfterSeconds

) {
}
