package com.app.rate_limiter.identity.users.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AppUser extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String email;

    @NotBlank
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Instant deletedAt;

}
