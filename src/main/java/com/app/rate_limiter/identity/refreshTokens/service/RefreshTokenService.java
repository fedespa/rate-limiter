package com.app.rate_limiter.identity.refreshTokens.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.util.HashUtils;
import com.app.rate_limiter.identity.refreshTokens.model.RefreshToken;
import com.app.rate_limiter.identity.refreshTokens.repository.RefreshTokenRepository;
import com.app.rate_limiter.identity.refreshTokens.response.RotatedRefreshToken;
import com.app.rate_limiter.identity.users.model.AppUser;
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

    public String create(AppUser user){
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashUtils.sha256(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
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

        AppUser user = oldToken.getUser();

        if (oldToken.isRevoked()) {
            this.refreshTokenSecurityService.handleTokenReuse(user);
            throw new AppException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
        }

        if (oldToken.isExpired()) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        oldToken.setRevoked(true);
        this.refreshTokenRepository.save(oldToken);

        String newRefreshToken = create(user);

        return new RotatedRefreshToken(
                user,
                newRefreshToken
        );
    }


}
