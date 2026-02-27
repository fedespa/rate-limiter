package com.app.rate_limiter.organization.apikeys.api;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.organization.apikeys.api.response.CreateApiKeyResponse;
import com.app.rate_limiter.organization.apikeys.request.CreateApiKeyRequest;
import com.app.rate_limiter.organization.apikeys.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CreateApiKeyResponse> createApiKey(
            @PathVariable UUID tenantId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        this.apiKeyService.create(tenantId, userDetails);

        return ResponseEntity.ok(new CreateApiKeyResponse());
    }


}
