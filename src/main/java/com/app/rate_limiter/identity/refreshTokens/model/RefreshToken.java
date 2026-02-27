package com.app.rate_limiter.identity.refreshTokens.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import com.app.rate_limiter.identity.users.model.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser user;

    @NotBlank
    private String tokenHash;

    @NotNull
    private Instant expiresAt;

    @NotNull
    @Builder.Default
    private boolean revoked = false;

    public boolean isExpired() {
        return this.expiresAt.isBefore(Instant.now());
    }

}
