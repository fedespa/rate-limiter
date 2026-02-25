package com.app.rate_limiter.identity.tokens.service;

import com.app.rate_limiter.common.util.HashUtils;
import com.app.rate_limiter.identity.tokens.model.UserToken;
import com.app.rate_limiter.identity.tokens.model.UserTokenType;
import com.app.rate_limiter.identity.tokens.repository.UserTokenRepository;
import com.app.rate_limiter.identity.users.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final HashUtils hashUtils;

    public String createVerificationToken(AppUser user) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = this.hashUtils.sha256(rawToken);

        UserToken verificationToken = UserToken.builder()
                .user(user)
                .type(UserTokenType.EMAIL_VERIFICATION)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        this.userTokenRepository.save(verificationToken);

        return rawToken;
    }

    public AppUser validateAndUseToken(String rawToken, UserTokenType type) {

        String tokenHash = this.hashUtils.sha256(rawToken);

        UserToken token = this.userTokenRepository.findByTokenHashAndType(tokenHash, type)
                .orElseThrow(() -> new RuntimeException("Token inválido o inexistente"));

        if (token.isRevoked() || token.getUsedAt() != null) {
            throw new AppException("El token ya ha sido utilizado o revocado");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException("El token ha expirado");
        }

        token.setUsedAt(Instant.now());
        token.setRevoked(true);

        this.userTokenRepository.save(token);

        return token.getUser();

    }

}
