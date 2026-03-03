package com.app.rate_limiter.core.ratelimit.service;

import com.app.rate_limiter.core.ratelimit.api.response.RateLimitResult;
import com.app.rate_limiter.organization.apikeys.api.dto.ApiKeyConfig;
import com.app.rate_limiter.organization.apikeys.service.ApiKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitServiceTest {

    @InjectMocks
    private RateLimitService rateLimitService;

    @Mock
    private ApiKeyService apiKeyService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("Should allow request when tokens are available.")
    void shouldAllowRequestWhenTokensAreAvailable(){

        String rawKey = "test-api-key";
        ApiKeyConfig mockConfig = new ApiKeyConfig(100, 10, UUID.randomUUID(), "hashed-key");

        when(apiKeyService.getApiKeyInfo(rawKey)).thenReturn(mockConfig);

        List<Long> redisResponse = List.of(1L, 99L);

        when(redisTemplate.execute(
                any(),
                anyList(),
                any(Object[].class)
        )).thenReturn(redisResponse);

        RateLimitResult result = this.rateLimitService.check(rawKey);

        assertTrue(result.allowed());
        assertEquals(99, result.remainingTokens());

    }

    @Test
    @DisplayName("Should allow 100 requests")
    void shouldAllow100Requests(){

        String rawKey = "test-api-key";
        ApiKeyConfig mockConfig = new ApiKeyConfig(100, 10, UUID.randomUUID(), "hashed-key");

        when(apiKeyService.getApiKeyInfo(rawKey)).thenReturn(mockConfig);

        when(redisTemplate.execute(
                any(),
                anyList(),
                any(Object[].class)
        )).thenAnswer(new Answer<List<Long>>() {
            private long remaining = 100;
            public List<Long> answer(InvocationOnMock invocation) {
                if (remaining > 0) {
                    remaining--;
                    return List.of(1L, remaining);
                } else {
                    return List.of(0L, 0L);
                }
            }
        });

        for (int i = 0; i < 100; i++) {
            RateLimitResult result = rateLimitService.check(rawKey);
            assertTrue(result.allowed(), "Petición " + (i + 1) + " debería ser permitida");
            assertEquals(99 - i, result.remainingTokens());
        }

        RateLimitResult rejectedResult = rateLimitService.check(rawKey);
        assertFalse(rejectedResult.allowed(), "La petición 101 debería ser rechazada");
        assertEquals(0, rejectedResult.remainingTokens(), "No deberían quedar tokens");

        verify(redisTemplate, times(101)).execute(any(), anyList(), any(Object[].class));

    }



}
