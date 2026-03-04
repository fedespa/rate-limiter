package com.app.rate_limiter.communication.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvitationMessage(

        @NotBlank
        @Email
        String to,

        @NotBlank
        String subject,

        @NotBlank
        String body

) {
}
