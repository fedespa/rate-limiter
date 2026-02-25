package com.app.rate_limiter.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
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
