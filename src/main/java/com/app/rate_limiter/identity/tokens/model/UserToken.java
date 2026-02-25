package com.app.rate_limiter.identity.tokens.model;

import com.app.rate_limiter.common.model.AuditableEntity;
import com.app.rate_limiter.identity.users.model.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_tokens")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserTokenType type;

    @NotBlank
    private String tokenHash;

    @NotNull
    private Instant expiresAt;

    private Instant usedAt;

    @NotNull
    @Builder.Default
    private boolean revoked = false;

}
