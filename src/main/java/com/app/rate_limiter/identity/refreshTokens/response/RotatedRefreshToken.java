package com.app.rate_limiter.identity.refreshTokens.response;


import com.app.rate_limiter.identity.users.model.AppUser;

import java.util.UUID;

public record RotatedRefreshToken(
        UUID userId,
        String rawRefreshToken,
        AppUser user
) {
}
