package com.app.rate_limiter.identity.users.request;

import java.util.UUID;

public record CreateAdminUserRequest(
        String email,
        String password,
        UUID tenantId
) {
}
