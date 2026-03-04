package com.app.rate_limiter.organization.invitations.api.controller;

import com.app.rate_limiter.organization.invitations.api.request.AcceptInvitationRequest;
import com.app.rate_limiter.organization.invitations.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/invitations")
public class InvitationAcceptanceController {

    private final InvitationService invitationService;

    @PostMapping("/{token}/accept")
    public ResponseEntity<?> acceptInvitation(
            @PathVariable String token,
            @Valid @RequestBody AcceptInvitationRequest request
    ){

        this.invitationService.accept(token, request);

        return ResponseEntity.ok().build();
    }


}
