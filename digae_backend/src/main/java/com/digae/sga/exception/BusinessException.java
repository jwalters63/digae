package com.digae.sga.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para violaciones de reglas de negocio.
 * El GlobalExceptionHandler la captura y retorna HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
