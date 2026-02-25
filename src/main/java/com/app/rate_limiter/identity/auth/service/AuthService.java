package com.app.rate_limiter.identity.auth.service;

import com.app.rate_limiter.identity.auth.api.response.RegisterResponse;
import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.tokens.service.UserTokenService;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.model.UserRole;
import com.app.rate_limiter.identity.users.repository.AppUserRepository;
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

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        Tenant tenant = this.tenantService.createTenant(new CreateTenantRequest(request.tenantName()));

        AppUser user = this.userService.createAdminUser(new CreateAdminUserRequest(request.email(), request.password(), tenant.getId()));

        // 3. Generar Token de Verificación
        String token = this.userTokenService.createVerificationToken(user);

        // 4. Enviar a RabbitMQ para proceso asíncrono (Communication Domain)

        return new RegisterResponse();


    }


}
