package com.app.rate_limiter.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex) {
        ApiError error = new ApiError(
                ex.getCode(),
                ex.getMessage(),
                Instant.now(),
                null
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Maneja errores de validación de Spring (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.put(error.getField(), error.getDefaultMessage())
        );

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                "Error en los campos enviados",
                Instant.now(),
                details
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex) {

        ApiError error = new ApiError(
                "MISSING_HEADER_ERROR",
                "Falta el encabezado obligatorio: " + ex.getHeaderName(),
                Instant.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ApiError error = new ApiError(
                "ACCESS_DENIED",
                "Accesso denegado",
                Instant.now(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // Maneja cualquier otro error inesperado (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Error no controlado: ", ex);
        ApiError error = new ApiError(
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error inesperado",
                Instant.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
