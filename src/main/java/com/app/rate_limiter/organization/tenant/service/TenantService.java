package com.app.rate_limiter.organization.tenant.service;

import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.model.TenantStatus;
import com.app.rate_limiter.organization.tenant.repository.TenantRepository;
import com.app.rate_limiter.organization.tenant.request.CreateTenantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public Tenant createTenant(CreateTenantRequest request){
        log.info("Iniciando creación de tenant: {}", request.name());

        if (this.tenantRepository.existsByName(request.name())) {
            // arrojar error
        }

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .status(TenantStatus.ACTIVE)
                .build();

        Tenant savedTenant = this.tenantRepository.save(tenant);

        log.info("Tenant creado exitosamente con ID: {}", savedTenant.getId());
        return savedTenant;
    }

}
