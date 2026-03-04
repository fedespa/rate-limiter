package com.app.rate_limiter.organization.invitations.repository;

import com.app.rate_limiter.organization.invitations.model.Invitation;
import com.app.rate_limiter.organization.invitations.model.InvitationStatus;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    boolean existsByEmailAndTenantAndStatus(String email, Tenant tenant, InvitationStatus status);

    Optional<Invitation> findByToken(@NotBlank String token);

}
