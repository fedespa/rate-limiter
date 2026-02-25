package com.app.rate_limiter.identity.users.request;

import com.app.rate_limiter.organization.tenant.model.Tenant;

public record CreateAdminUserRequest(
        String email,
        String password,
        Tenant tenant
) {
}
