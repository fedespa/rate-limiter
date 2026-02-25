package com.app.rate_limiter.identity.users.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.identity.tokens.model.UserTokenType;
import com.app.rate_limiter.identity.tokens.repository.UserTokenRepository;
import com.app.rate_limiter.identity.tokens.service.UserTokenService;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.model.UserRole;
import com.app.rate_limiter.identity.users.repository.AppUserRepository;
import com.app.rate_limiter.identity.users.request.CreateAdminUserRequest;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;

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

    public void verifyUser(String rawToken) {
        AppUser user = this.userTokenService.validateAndUseToken(rawToken, UserTokenType.EMAIL_VERIFICATION);

        if (user.getVerifiedAt() != null) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        user.setVerifiedAt(Instant.now());

        this.appUserRepository.save(user);
    }

}
