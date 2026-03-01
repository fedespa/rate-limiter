package com.app.rate_limiter.organization.apikeys.repository;

import com.app.rate_limiter.organization.apikeys.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    @Query("""
        SELECT a FROM ApiKey a
        JOIN FETCH a.tenant t
        JOIN FETCH t.plan p
        WHERE a.keyHash = :keyHash
    """)
    Optional<ApiKey> findByKeyHash(@Param("keyHash") String keyHash);

}
