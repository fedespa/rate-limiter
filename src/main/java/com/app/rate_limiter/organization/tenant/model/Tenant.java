package com.app.rate_limiter.organization.tenant.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "tenants")
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tenant extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TenantStatus status;

}
