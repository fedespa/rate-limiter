package com.app.rate_limiter.identity.refreshTokens.response;

import com.app.rate_limiter.identity.users.model.AppUser;

public record RotatedRefreshToken(
        AppUser user,
        String rawRefreshToken
) {
}
