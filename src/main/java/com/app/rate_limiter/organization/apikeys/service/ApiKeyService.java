package com.app.rate_limiter.organization.apikeys.service;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.organization.apikeys.api.response.CreateApiKeyResponse;
import com.app.rate_limiter.organization.apikeys.repository.ApiKeyRepository;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.service.TenantAccessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final TenantAccessValidator tenantAccessValidator;

    public CreateApiKeyResponse create(UUID tenantId, CustomUserDetails userDetails){
        Tenant tenant = this.tenantAccessValidator.validateAndGetTenant(tenantId, userDetails);

        return new CreateApiKeyResponse();
    }

}
