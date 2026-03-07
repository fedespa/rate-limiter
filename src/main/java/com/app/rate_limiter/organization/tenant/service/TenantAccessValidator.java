package com.app.rate_limiter.organization.tenant.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.identity.users.model.UserRole;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.model.TenantStatus;
import com.app.rate_limiter.organization.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantAccessValidator {

    private final TenantRepository tenantRepository;

    public Tenant validateAndGetTenant(UUID tenantId, CustomUserDetails userDetails) {

        Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new AppException(ErrorCode.TENANT_SUSPENDED);
        }

        UUID userTenantId = userDetails.getTenantId();

        boolean isSystemAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .anyMatch(authority -> authority.equals(UserRole.ROLE_SYSTEM_ADMIN.name()));

        if (!tenant.getId().equals(userTenantId) && !isSystemAdmin) {
            throw new AppException(ErrorCode.CROSS_TENANT_ACCESS);
        }

        return tenant;

    }

}
