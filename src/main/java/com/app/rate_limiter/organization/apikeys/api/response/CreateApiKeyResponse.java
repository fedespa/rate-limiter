package com.app.rate_limiter.organization.apikeys.api.response;

import java.util.UUID;

public record CreateApiKeyResponse(
        String apiKey,
        UUID keyId
) {
}
