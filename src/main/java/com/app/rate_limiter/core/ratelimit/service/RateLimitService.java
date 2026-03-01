package com.app.rate_limiter.core.ratelimit.service;

import com.app.rate_limiter.core.ratelimit.api.response.RateLimitResult;
import com.app.rate_limiter.organization.apikeys.api.dto.ApiKeyConfig;
import com.app.rate_limiter.organization.apikeys.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final ApiKeyService apiKeyService;

    private final RedisTemplate<String, String> redisTemplate;

    private final DefaultRedisScript<List> rateLimitScript;

    public RateLimitResult check(String rawApiKey){

        ApiKeyConfig config = this.apiKeyService.getApiKeyInfo(rawApiKey);

        String redisKey = "rl:" + config.tenantId() + ":" + config.keyHash();

        List<String> args = List.of(
          String.valueOf(config.burstCapacity()),
          String.valueOf(config.refillRate()),
          String.valueOf(Instant.now().getEpochSecond()),
          "1"
        );

        try {
            List<Long> result = this.redisTemplate.execute(
                    this.rateLimitScript,
                    List.of(redisKey),
                    args.toArray()
            );

            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Redis devolvió una respuesta vacía");
            }

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();

            return new RateLimitResult(allowed, remaining, config);
        } catch (Exception e) {
            log.error("Fallo en Redis Rate Limit (Fail-Open activado) para Tenant {}: {}",
                    config.tenantId(), e.getMessage());
            return new RateLimitResult(true, config.burstCapacity(), config);
        }

    }

}
