package com.app.rate_limiter.identity.auth.service;

import com.app.rate_limiter.common.security.CustomUserDetails;
import com.app.rate_limiter.common.util.jwt.CreateJWTTokenDto;
import com.app.rate_limiter.common.util.jwt.JwtUtils;
import com.app.rate_limiter.communication.email.producer.EmailProducer;
import com.app.rate_limiter.identity.auth.api.response.RegisterResponse;
import com.app.rate_limiter.identity.auth.api.response.TokensResponse;
import com.app.rate_limiter.identity.auth.request.LoginRequest;
import com.app.rate_limiter.identity.auth.request.RegisterRequest;
import com.app.rate_limiter.identity.refreshTokens.response.RotatedRefreshToken;
import com.app.rate_limiter.identity.refreshTokens.service.RefreshTokenService;
import com.app.rate_limiter.identity.userTokens.service.UserTokenService;
import com.app.rate_limiter.identity.users.model.AppUser;
import com.app.rate_limiter.identity.users.request.CreateAdminUserRequest;
import com.app.rate_limiter.identity.users.service.UserService;
import com.app.rate_limiter.organization.tenant.model.Tenant;
import com.app.rate_limiter.organization.tenant.request.CreateTenantRequest;
import com.app.rate_limiter.organization.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantService tenantService;
    private final UserTokenService userTokenService;
    private final UserService userService;
    private final EmailProducer emailProducer;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

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

    @Transactional
    public TokensResponse login(LoginRequest request) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        if (!authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        CreateJWTTokenDto createJWTTokenDto = new CreateJWTTokenDto(
                userDetails.getUsername(),
                userDetails.isVerified(),
                userDetails.getUserId().toString()
        );

        String accessToken = this.jwtUtils.generateToken(createJWTTokenDto);
        String refreshToken = this.refreshTokenService.create(userDetails.getUser());

        return new TokensResponse(accessToken, refreshToken);
    }

    public TokensResponse refreshToken(String rawRefreshToken) {

        RotatedRefreshToken rotated = this.refreshTokenService.rotateToken(rawRefreshToken);

        CreateJWTTokenDto dto = new CreateJWTTokenDto(
                rotated.user().getEmail(),
                rotated.user().getVerifiedAt() != null,
                rotated.user().getId().toString()
        );

        String accessToken = this.jwtUtils.generateToken(dto);

        return new TokensResponse(accessToken, rotated.rawRefreshToken());
    }

}
