package com.app.rate_limiter.common.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private final static long expirationTime = 5 * 60 * 1000;

    @Value("${jwt.secret}")
    private String key = "secret";

    private final String issuer = "SYSTEM";

    public String generateToken(CreateJWTTokenDto dto) {
        Algorithm algorithm = Algorithm.HMAC256(key);

        long now = System.currentTimeMillis();
        Date expiryDate = new Date(now + expirationTime);

        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(expiryDate)
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date())
                .withClaim("email", dto.email())
                .withClaim("verified", dto.verified())
                .withClaim("roles", dto.roles())
                .withClaim("tenantId", dto.tenantId())
                .withClaim("deleted", dto.deleted())
                .withSubject(dto.userId())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(key);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(this.issuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);

        return jwt;
    }

    public String extractEmail(DecodedJWT token) {
        return token.getClaim("email").asString();
    }

}
