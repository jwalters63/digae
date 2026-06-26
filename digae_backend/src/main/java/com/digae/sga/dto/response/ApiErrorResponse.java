package com.digae.sga.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura unificada para respuestas de error de la API.
 * Usada por el GlobalExceptionHandler para formatear errores consistentemente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /** Detalle de errores de validación: campo → mensaje */
    private Map<String, String> validationErrors;
}
