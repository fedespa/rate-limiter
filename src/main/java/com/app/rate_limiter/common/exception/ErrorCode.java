package com.app.rate_limiter.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Identity errors
    USER_NOT_FOUND("ID_001", "Usuario no encontrado", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("ID_002", "El email ya está registrado", HttpStatus.CONFLICT),
    INVALID_TOKEN("ID_003", "Token inválido o expirado", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("ID_004", "Credenciales incorrectas", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_VERIFIED("ID_005", "La cuenta no ha sido verificada", HttpStatus.FORBIDDEN),
    ACCOUNT_DELETED("ID_006", "La cuenta fue eliminada", HttpStatus.GONE),
    REFRESH_TOKEN_INVALID("ID_007", "El refresh token es inválido o fue revocado", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("ID_008", "El refresh token ha expirado", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REUSE_DETECTED("ID_009", "Reutilización de refresh token detectada, sesiones revocadas", HttpStatus.UNAUTHORIZED),
    PASSWORD_TOO_WEAK("ID_010", "La contraseña no cumple los requisitos mínimos de seguridad", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED("ID_011", "El usuario ya fue verificado", HttpStatus.CONFLICT),

    TOKEN_NOT_FOUND("UT_001", "El token no existe o es inválido", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED("UT_002", "El token ha expirado", HttpStatus.GONE),
    TOKEN_ALREADY_USED("UT_003", "El token ya fue utilizado", HttpStatus.CONFLICT),
    TOKEN_REVOKED("UT_004", "El token fue revocado", HttpStatus.UNAUTHORIZED),

    // Organization Errors
    TENANT_NOT_FOUND("ORG_001", "La organización no existe", HttpStatus.NOT_FOUND),
    TENANT_SUSPENDED("ORG_002", "La organización se encuentra suspendida", HttpStatus.FORBIDDEN),
    TENANT_INACTIVE("ORG_003", "La organización está inactiva", HttpStatus.FORBIDDEN),

    // API Key Errors
    API_KEY_NOT_FOUND("KEY_001", "La API Key no existe", HttpStatus.UNAUTHORIZED),
    API_KEY_REVOKED("KEY_002", "La API Key ha sido revocada", HttpStatus.UNAUTHORIZED),
    API_KEY_INVALID_FORMAT("KEY_003", "Formato de API Key inválido", HttpStatus.BAD_REQUEST),

    // Plan Errors
    PLAN_NOT_FOUND("PLAN_001", "El plan no existe", HttpStatus.NOT_FOUND),
    PLAN_INVALID_CAPACITY("PLAN_002", "La capacidad del plan debe ser mayor a cero", HttpStatus.BAD_REQUEST),
    PLAN_INVALID_REFILL_RATE("PLAN_003", "El refill rate debe ser mayor a cero", HttpStatus.BAD_REQUEST),

    // Core Errors
    RATE_LIMIT_EXCEEDED("CORE_001", "Demasiadas peticiones", HttpStatus.TOO_MANY_REQUESTS),

    // Authorization Errors
    ACCESS_DENIED("AUTH_001", "No tenés permisos para realizar esta acción", HttpStatus.FORBIDDEN),
    CROSS_TENANT_ACCESS("AUTH_002", "No podés acceder a recursos de otra organización", HttpStatus.FORBIDDEN),
    ROLE_NOT_ALLOWED("AUTH_003", "Tu rol no tiene permitido ejecutar esta operación", HttpStatus.FORBIDDEN),

    // Invitation Errors
    INVITATION_NOT_FOUND("INV_001", "La invitación no existe o ya fue usada", HttpStatus.NOT_FOUND),
    INVITATION_EXPIRED("INV_002", "La invitación ha expirado", HttpStatus.GONE),
    INVITATION_ALREADY_ACCEPTED("INV_003", "La invitación ya fue aceptada", HttpStatus.CONFLICT),
    INVITATION_EMAIL_MISMATCH("INV_004", "El email no coincide con el de la invitación", HttpStatus.BAD_REQUEST),

    // Infrastructure Errors
    REDIS_UNAVAILABLE("INFRA_001", "El servicio de caché no está disponible", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_UNAVAILABLE("INFRA_002", "El servicio de base de datos no está disponible", HttpStatus.SERVICE_UNAVAILABLE),
    EMAIL_SERVICE_UNAVAILABLE("INFRA_003", "El servicio de email no está disponible", HttpStatus.SERVICE_UNAVAILABLE),

    // General Errors
    VALIDATION_ERROR("GEN_001", "Error de validación en los datos enviados", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("GEN_002", "El recurso solicitado no existe", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("GEN_003", "Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

}
