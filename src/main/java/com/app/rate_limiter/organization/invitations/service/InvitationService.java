package com.app.rate_limiter.organization.invitations.service;

import com.app.rate_limiter.common.exception.AppException;
import com.app.rate_limiter.common.exception.ErrorCode;
import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.common.security.annotation.RequiresVerificationAndAdmin;
import com.app.rate_limiter.common.util.HashUtils;
import com.app.rate_limiter.communication.email.producer.EmailProducer;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.repository.AppUserRepository;
import com.app.rate_limiter.identity.users.service.UserService;
import com.app.rate_limiter.organization.invitations.api.request.AcceptInvitationRequest;
import com.app.rate_limiter.organization.invitations.api.request.CreateInvitationRequest;
import com.app.rate_limiter.organization.invitations.model.Invitation;
import com.app.rate_limiter.organization.invitations.model.InvitationStatus;
import com.app.rate_limiter.organization.invitations.repository.InvitationRepository;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.service.TenantAccessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final AppUserRepository appUserRepository;
    private final HashUtils hashUtils;
    private final TenantAccessValidator tenantAccessValidator;
    private final UserService userService;
    private final EmailProducer emailProducer;

    @Transactional
    @RequiresVerificationAndAdmin
    public void create(String tenantId, CustomUserDetails userDetails, CreateInvitationRequest request){

        Tenant tenant = this.tenantAccessValidator.validateAndGetTenant(UUID.fromString(tenantId), userDetails);

        Optional<AppUser> user = this.appUserRepository.findByEmail(request.email());

        if (user.isPresent() && user.get().getTenant() != null){

            if (user.get().getTenant().getId().equals(tenant.getId())){
                throw new AppException(ErrorCode.ALREADY_MEMBER);
            }

            throw new AppException(ErrorCode.USER_ALREADY_ASSIGNED);
        }

        boolean alreadyInvited = this.invitationRepository.existsByEmailAndTenantAndStatus(
                request.email(),
                tenant,
                InvitationStatus.PENDING
        );

        if (alreadyInvited) {
            throw new AppException(ErrorCode.INVITATION_ALREADY_PENDING);
        }

        UUID rawToken = UUID.randomUUID();

        String hashToken = this.hashUtils.sha256(rawToken.toString());

        Invitation invitation = Invitation.builder()
                .email(request.email())
                .tenant(tenant)
                .status(InvitationStatus.PENDING)
                .roleToAssign(request.roleToAssign())
                .token(hashToken)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        this.invitationRepository.save(invitation);

        this.emailProducer.sendInvitationEmail(request.email(), tenant.getName(), String.valueOf(rawToken));
    }

    @Transactional
    public void accept(String token, AcceptInvitationRequest request){

        String hashToken = this.hashUtils.sha256(token);

        Invitation invitation = this.invitationRepository.findByToken(hashToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVITATION_NOT_FOUND));

        if (invitation.isExpired()) {
            throw new AppException(ErrorCode.INVITATION_EXPIRED);
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new AppException(ErrorCode.INVITATION_ALREADY_ACCEPTED);
        }

        Optional<AppUser> optionalUser = this.appUserRepository.findByEmail(invitation.getEmail());

        if (optionalUser.isEmpty()){

            this.userService.createUserFromInvitation(
                    invitation.getEmail(),
                    request.password(),
                    invitation.getTenant(),
                    invitation.getRoleToAssign()
            );

        } else {
            AppUser user = optionalUser.get();

            if (user.getTenant() != null &&
                    !user.getTenant().getId().equals(invitation.getTenant().getId())) {
                throw new AppException(ErrorCode.USER_ALREADY_ASSIGNED);
            }

            user.setTenant(invitation.getTenant());

            this.appUserRepository.save(user);
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
    }

}
