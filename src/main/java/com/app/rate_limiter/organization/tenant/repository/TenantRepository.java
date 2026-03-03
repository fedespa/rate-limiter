package com.app.rate_limiter.organization.tenant.repository;

import com.app.rate_limiter.organization.tenant.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    boolean existsByName(String name);

    Optional<Tenant> findById(UUID id);

    @Query("SELECT t FROM Tenant t JOIN FETCH t.plan")
    Page<Tenant> findAll(Pageable pageable);

}
