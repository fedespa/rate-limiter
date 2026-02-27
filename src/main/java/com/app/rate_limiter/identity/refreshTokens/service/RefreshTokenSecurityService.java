package com.app.rate_limiter.identity.refreshTokens.service;

import com.app.rate_limiter.identity.refreshTokens.repository.RefreshTokenRepository;
import com.app.rate_limiter.identity.users.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenSecurityService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleTokenReuse(AppUser user){
        log.warn("Alerta, intento de reutilización de Refresh Token para el usuario: {}", user.getEmail());
        this.refreshTokenRepository.revokeAllUserTokens(user.getId());
    }

}
