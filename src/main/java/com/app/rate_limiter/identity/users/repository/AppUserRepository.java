package com.app.rate_limiter.identity.users.repository;

import com.app.rate_limiter.identity.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    @Query("""
        SELECT u FROM AppUser u
        JOIN FETCH u.tenant t
        WHERE u.id = :id
    """)
    Optional<AppUser> findByIdWithTenant(@Param("id") UUID id);

}
