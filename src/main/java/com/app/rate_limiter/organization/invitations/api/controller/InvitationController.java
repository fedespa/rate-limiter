package com.app.rate_limiter.organization.invitations.api.controller;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.organization.invitations.api.request.CreateInvitationRequest;
import com.app.rate_limiter.organization.invitations.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/tenants/{tenantId}/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<?> invite(
            @PathVariable("tenantId") String tenantId,
            @Valid @RequestBody CreateInvitationRequest createInvitationRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){

        this.invitationService.create(tenantId, userDetails, createInvitationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
