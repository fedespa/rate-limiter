package com.app.rate_limiter.organization.tenant.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest (

        @NotBlank
        String name

) {
}
