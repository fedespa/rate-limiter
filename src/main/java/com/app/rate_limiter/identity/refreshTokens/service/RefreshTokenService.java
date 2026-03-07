package com.app.rate_limiter.identity.refreshTokens.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.util.HashUtils;
import com.app.rate_limiter.identity.refreshTokens.model.RefreshToken;
import com.app.rate_limiter.identity.refreshTokens.repository.RefreshTokenRepository;
import com.app.rate_limiter.identity.refreshTokens.response.RotatedRefreshToken;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final HashUtils hashUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenSecurityService refreshTokenSecurityService;
    private final AppUserRepository appUserRepository;

    public String create(UUID userId){
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashUtils.sha256(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        this.refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RotatedRefreshToken rotateToken(String rawToken){
        String tokenHash = this.hashUtils.sha256(rawToken);

        RefreshToken oldToken = this.refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_INVALID));

        UUID userId = oldToken.getUserId();

        AppUser user = this.appUserRepository.findByIdWithTenant(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }

        if (oldToken.isRevoked()) {
            this.refreshTokenSecurityService.handleTokenReuse(userId);
            throw new AppException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
        }

        if (oldToken.isExpired()) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        oldToken.setRevoked(true);
        this.refreshTokenRepository.save(oldToken);

        String newRefreshToken = create(userId);

        return new RotatedRefreshToken(
                userId,
                newRefreshToken,
                user
        );
    }


}
