package com.app.rate_limiter.organization.apikeys.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.common.util.HashUtils;
import com.app.rate_limiter.organization.apikeys.api.dto.ApiKeyConfig;
import com.app.rate_limiter.organization.apikeys.api.response.CreateApiKeyResponse;
import com.app.rate_limiter.organization.apikeys.model.ApiKey;
import com.app.rate_limiter.organization.apikeys.repository.ApiKeyRepository;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.service.TenantAccessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final TenantAccessValidator tenantAccessValidator;
    private final HashUtils hashUtils;

    @Transactional
    public CreateApiKeyResponse create(UUID tenantId, CustomUserDetails userDetails){
        Tenant tenant = this.tenantAccessValidator.validateAndGetTenant(tenantId, userDetails);

        String rawApikey = generateApiKey();
        String hashedApikey = this.hashUtils.sha256(rawApikey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(hashedApikey)
                .tenant(tenant)
                .build();

        this.apiKeyRepository.save(apiKey);

        return new CreateApiKeyResponse(rawApikey, apiKey.getId());
    }

    @Cacheable(value = "api_keys", key = "#rawApiKey")
    public ApiKeyConfig getApiKeyInfo(String rawApiKey) {

        String hashedApikey = this.hashUtils.sha256(rawApiKey);

        return this.apiKeyRepository.findByKeyHash(hashedApikey)
                .map(key -> {
                    var plan = key.getTenant().getPlan();

                    return new ApiKeyConfig(
                            plan.getCapacity(),
                            plan.getRefillRate(),
                            key.getTenant().getId(),
                            key.getKeyHash()
                    );
                })
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_API_KEY));
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return "rk_live_" +
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);
    }

}
