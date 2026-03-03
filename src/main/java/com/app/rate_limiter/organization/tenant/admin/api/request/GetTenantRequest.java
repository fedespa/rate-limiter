package com.app.rate_limiter.organization.tenant.admin.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GetTenantRequest(
        @NotNull
        @Min(0)
        int page
) {
}
