package com.app.rate_limiter.core.ratelimit.api.response;

import com.app.rate_limiter.core.ratelimit.model.CheckResponse;
import com.app.rate_limiter.organization.apikeys.api.dto.ApiKeyConfig;

public record RateLimitResult(
        boolean allowed,
        int remainingTokens,
        ApiKeyConfig config
) {

    public CheckResponse toResponse() {
        return new CheckResponse(allowed, remainingTokens, allowed ? 0 : 1);
    }

}
