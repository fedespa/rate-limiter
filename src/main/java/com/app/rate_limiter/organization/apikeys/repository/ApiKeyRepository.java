package com.app.rate_limiter.organization.apikeys.repository;

import com.app.rate_limiter.organization.apikeys.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
}
