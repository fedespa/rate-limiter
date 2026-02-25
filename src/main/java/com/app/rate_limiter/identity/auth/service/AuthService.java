package com.app.rate_limiter.identity.auth.service;

import com.app.rate_limiter.communication.email.producer.EmailProducer;
import com.app.rate_limiter.identity.auth.api.response.LoginResponse;
import com.app.rate_limiter.identity.auth.api.response.RegisterResponse;
import com.app.rate_limiter.identity.auth.request.LoginRequest;
import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.tokens.model.UserTokenType;
import com.app.rate_limiter.identity.tokens.service.UserTokenService;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.request.CreateAdminUserRequest;
import com.app.rate_limiter.identity.users.service.UserService;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.request.CreateTenantRequest;
import com.app.rate_limiter.organization.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantService tenantService;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;
    private final UserService userService;
    private final EmailProducer emailProducer;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        Tenant tenant = this.tenantService.createTenant(new CreateTenantRequest(request.tenantName()));

        AppUser user = this.userService.createAdminUser(new CreateAdminUserRequest(request.email(), request.password(), tenant));

        String token = this.userTokenService.createVerificationToken(user);

        this.emailProducer.sendVerificationEmail(request.email(), token);

        return new RegisterResponse("Registrado exitosamente!");
    }

    @Transactional
    public void verify(String rawToken) {
        this.userService.verifyUser(rawToken);
    }


    public LoginResponse login(LoginRequest request) {

    }


}
