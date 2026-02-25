package com.app.rate_limiter.identity.users.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.model.UserRole;
import com.app.rate_limiter.identity.users.repository.AppUserRepository;
import com.app.rate_limiter.identity.users.request.CreateAdminUserRequest;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser createAdminUser(CreateAdminUserRequest request) {

        String hashPassword = this.passwordEncoder.encode(request.password());

        if (this.appUserRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        AppUser appUser = AppUser.builder()
                .tenant(request.tenant())
                .email(request.email())
                .passwordHash(hashPassword)
                .role(UserRole.ROLE_TENANT_ADMIN)
                .build();

        AppUser adminUser = this.appUserRepository.save(appUser);

        return adminUser;

    }

}
