package com.app.rate_limiter.organization.invitations.api.request;

import jakarta.validation.constraints.NotBlank;

public record AcceptInvitationRequest(

        @NotBlank
        String password

) {
}
