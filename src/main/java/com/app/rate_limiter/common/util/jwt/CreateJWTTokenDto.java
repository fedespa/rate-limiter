package com.app.rate_limiter.common.util.jwt;

public record CreateJWTTokenDto(
        String email,
        boolean verified,
        String userId
){
}
