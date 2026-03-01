package com.app.rate_limiter.organization.apikeys.api.dto;

import java.util.UUID;

public record ApiKeyConfig(
        int burstCapacity,
        int refillRate,
        UUID tenantId,
        String keyHash
) {
}
