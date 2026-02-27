package com.app.rate_limiter.organization.apikeys.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import com.app.rate_limiter.organization.plan.model.Plan;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ApiKey extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    private String keyHash;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @NotNull
    @Builder.Default
    private boolean revoked = false;

}
