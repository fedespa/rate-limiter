package com.app.rate_limiter.organization.tenant.admin.api;

import com.app.rate_limiter.common.model.response.PageResponse;
import com.app.rate_limiter.organization.tenant.admin.api.request.GetTenantRequest;
import com.app.rate_limiter.organization.tenant.admin.api.request.SubscribeTenantRequest;
import com.app.rate_limiter.organization.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/tenants")
@RequiredArgsConstructor
public class TenantSystemAdminController {

    private final TenantService tenantService;

    @PostMapping("/{tenantId}/subscribe")
    public ResponseEntity<?> subscribe(
            @PathVariable("tenantId") String tenantId,
            @Valid @RequestBody SubscribeTenantRequest subscribeTenantRequest
    ) {
        this.tenantService.assignPlan(tenantId, subscribeTenantRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse> getTenants(
            @RequestParam(defaultValue = "0") int page
    ){
        GetTenantRequest request = new GetTenantRequest(page);

        PageResponse response = this.tenantService.getTenants(request);

        return ResponseEntity.ok(response);
    }

}
