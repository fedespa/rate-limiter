package com.app.rate_limiter.identity.userTokens.repository;

import com.app.rate_limiter.identity.userTokens.model.UserToken;
import com.app.rate_limiter.identity.userTokens.model.UserTokenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByTokenHashAndType(@NotBlank String tokenHash, @NotNull UserTokenType type);

}
