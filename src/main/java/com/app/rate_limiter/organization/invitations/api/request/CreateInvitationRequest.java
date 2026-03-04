package com.app.rate_limiter.organization.invitations.api.request;

import com.app.rate_limiter.identity.users.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInvitationRequest(

        @NotBlank
        @Email
        String email,

        @NotNull
        UserRole roleToAssign

) {
}
