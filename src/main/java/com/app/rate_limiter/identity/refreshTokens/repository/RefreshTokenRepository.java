package com.app.rate_limiter.identity.refreshTokens.repository;

import com.app.rate_limiter.identity.refreshTokens.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("""
        SELECT rt FROM RefreshToken rt
        JOIN FETCH rt.user
        WHERE rt.tokenHash = :tokenHash
    """)
    Optional<RefreshToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllUserTokens(@Param("userId") UUID userId);

}
