package org.ups.liberacionesobra.presentation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ups.liberacionesobra.domain.exception.SolicitudDuplicadaException;
import org.ups.liberacionesobra.domain.exception.SolicitudNotFoundException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SolicitudNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SolicitudNotFoundException ex) {
        log.warn("Solicitud no encontrada: {}", ex.getSolicitudId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensaje",    ex.getMessage(),
                "codigoError", "SOLICITUD_NO_ENCONTRADA"
        ));
    }

    @ExceptionHandler(SolicitudDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(SolicitudDuplicadaException ex) {
        log.warn("Solicitud duplicada detectada, existente: {}", ex.getSolicitudExistenteId());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "mensaje",              ex.getMessage(),
                "codigoError",          "SOLICITUD_DUPLICADA",
                "solicitudExistenteId", ex.getSolicitudExistenteId().toString()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(this::campoError)
                .toList();
        log.warn("Error de validación: {} campo(s) inválido(s)", errores.size());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "mensaje", "Error de validación en los datos de entrada",
                "errores", errores
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensaje",    ex.getMessage(),
                "codigoError", "RECURSO_NO_ENCONTRADO"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "mensaje",    "Error interno del servidor",
                "codigoError", "ERROR_INTERNO"
        ));
    }

    private Map<String, String> campoError(FieldError fe) {
        return Map.of(
                "campo",   fe.getField(),
                "mensaje", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido"
        );
    }
}
