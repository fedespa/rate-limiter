package com.app.rate_limiter.organization.apikeys.request;

import java.util.UUID;

public record CreateApiKeyRequest(
        UUID planId
) {
}
