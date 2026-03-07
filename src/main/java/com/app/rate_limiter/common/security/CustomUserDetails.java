package com.app.rate_limiter.common.security;

import com.app.rate_limiter.identity.users.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final boolean verified;
    private final List<GrantedAuthority> authorities;
    private final UUID tenantId;
    private final boolean deleted;
    private final String passwordHash;

    public CustomUserDetails(AppUser user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.verified = user.getVerifiedAt() != null;
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        this.tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        this.deleted = user.getDeletedAt() != null;
        this.passwordHash = user.getPasswordHash();

    }

    public CustomUserDetails(UUID userId, String email, boolean verified, List<GrantedAuthority> authorities, UUID tenantId, boolean deleted) {
        this.userId = userId;
        this.email = email;
        this.verified = verified;
        this.authorities = authorities;
        this.tenantId = tenantId;
        this.deleted = deleted;
        this.passwordHash = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isVerified() {
        return verified;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }
}
