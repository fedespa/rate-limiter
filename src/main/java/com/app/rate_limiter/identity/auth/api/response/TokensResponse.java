package com.app.rate_limiter.identity.auth.api.response;

public record TokensResponse(
        String accessToken,
        String refreshToken
) {
}
