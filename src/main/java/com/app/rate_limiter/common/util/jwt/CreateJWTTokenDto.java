package com.app.rate_limiter.common.util.jwt;

import java.util.List;

public record CreateJWTTokenDto(
        String email,
        boolean verified,
        String userId,
        List<String> roles,
        String tenantId,
        boolean deleted
){
}
