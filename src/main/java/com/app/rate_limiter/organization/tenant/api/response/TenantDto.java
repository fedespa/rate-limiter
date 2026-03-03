package com.app.rate_limiter.organization.tenant.api.response;

import com.app.rate_limiter.organization.plan.api.response.PlanDto;
import com.app.rate_limiter.organization.tenant.model.TenantStatus;

public record TenantDto(
        String uuid,
        String name,
        TenantStatus status,
        PlanDto plan
) {
}
