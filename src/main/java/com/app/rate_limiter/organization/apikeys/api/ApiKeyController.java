package com.app.rate_limiter.organization.apikeys.api;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.organization.apikeys.model.ApiKey;
import com.app.rate_limiter.organization.apikeys.request.CreateApiKeyRequest;
import com.app.rate_limiter.organization.apikeys.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/api/tenants/{tenantId}/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('TENANT_DEVELOPER')")
    public void createApiKey(
            @PathVariable UUID tenantId,
            @RequestBody CreateApiKeyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        this.apiKeyService.create(tenantId, request);

    }


}

//@Component
//@RequiredArgsConstructor
//public class TenantAccessValidator {
//
//    private final TenantRepository tenantRepository;
//
//    public Tenant validateAndGetTenant(UUID tenantId, CustomUserDetails userDetails) {
//
//        Tenant tenant = tenantRepository.findById(tenantId)
//                .orElseThrow(() -> ErrorCode.TENANT_NOT_FOUND);
//
//        if (tenant.getStatus() != TenantStatus.ACTIVE) {
//            throw ErrorCode.TENANT_SUSPENDED;
//        }
//
//        UUID userTenantId = userDetails.getUser().getTenant().getId();
//
//        if (!tenantId.equals(userTenantId)
//                && userDetails.getUser().getRole() != UserRole.ROLE_SYSTEM_ADMIN) {
//            throw ErrorCode.CROSS_TENANT_ACCESS;
//        }
//
//        return tenant;
//    }
//}
