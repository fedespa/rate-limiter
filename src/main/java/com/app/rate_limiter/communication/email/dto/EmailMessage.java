package com.app.rate_limiter.communication.email.dto;

public record EmailMessage(
        String to,
        String subject,
        String body
) {
}
