package com.app.rate_limiter.organization.apikeys.service;

import com.app.rate_limiter.organization.apikeys.repository.ApiKeyRepository;
import com.app.rate_limiter.organization.apikeys.request.CreateApiKeyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public void create(UUID tenantId, CreateApiKeyRequest request){



    }

}
