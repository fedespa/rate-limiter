package com.app.rate_limiter.organization.tenant.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.model.response.PageResponse;
import com.app.rate_limiter.organization.plan.api.response.PlanDto;
import com.app.rate_limiter.organization.plan.model.Plan;
import com.app.rate_limiter.organization.plan.repository.PlanRepository;
import com.app.rate_limiter.organization.tenant.admin.api.request.GetTenantRequest;
import com.app.rate_limiter.organization.tenant.admin.api.request.SubscribeTenantRequest;
import com.app.rate_limiter.organization.tenant.api.response.TenantDto;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.model.TenantStatus;
import com.app.rate_limiter.organization.tenant.repository.TenantRepository;
import com.app.rate_limiter.organization.tenant.request.CreateTenantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;

    public Tenant createTenant(CreateTenantRequest request){
        log.info("Iniciando creación de tenant: {}", request.name());

        if (this.tenantRepository.existsByName(request.name())) {
            throw new AppException(ErrorCode.TENANT_ALREADY_EXISTS);
        }

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .status(TenantStatus.ACTIVE)
                .build();

        Tenant savedTenant = this.tenantRepository.save(tenant);

        log.info("Tenant creado exitosamente con ID: {}", savedTenant.getId());
        return savedTenant;
    }

    @Transactional
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void assignPlan(String tenantId, SubscribeTenantRequest request){

        log.info("Iniciando la asignación del plan para el tenant: {}", tenantId);

        Tenant tenant = this.tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.ACTIVE){
            throw new AppException(ErrorCode.TENANT_SUSPENDED);
        }

        Plan plan = this.planRepository.findById(UUID.fromString(request.planId()))
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        tenant.setPlan(plan);
        this.tenantRepository.save(tenant);

        log.info("Plan {} asignado a tenant con ID {} exitosamente", plan.getName(), tenant.getId());
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public PageResponse getTenants(GetTenantRequest request){

        Pageable pageable = PageRequest.of(request.page(), 20);

        Page<Tenant> tenants = this.tenantRepository.findAll(pageable);

        List<TenantDto> tenantDtos = tenants.stream().map(t -> convertToDto(t)).toList();

        return new PageResponse(
                tenantDtos,
                tenants.getNumber(),
                tenants.getSize(),
                tenants.getTotalElements(),
                tenants.getTotalPages()
        );
    };

    private TenantDto convertToDto(Tenant tenant){
        Plan plan = tenant.getPlan();

        return new TenantDto(
                tenant.getId().toString(),
                tenant.getName(),
                tenant.getStatus(),
                new PlanDto(
                        plan.getId().toString(),
                        plan.getName()
                )
        );
    }

}
