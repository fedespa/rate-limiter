package com.app.rate_limiter.organization.invitations.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import com.app.rate_limiter.identity.users.model.UserRole;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@AllArgsConstructor @NoArgsConstructor
@Getter
@Setter
@Builder
public class Invitation extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Email
    private String email;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole roleToAssign;

    @NotBlank
    private String token;

    private Instant expiresAt;

    @Version
    @Column(nullable = false)
    private Long version;


    public boolean isExpired() {
        return this.expiresAt.isBefore(Instant.now());
    }

}
