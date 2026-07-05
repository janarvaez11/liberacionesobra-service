package org.ups.liberacionesobra.presentation.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.ups.liberacionesobra.domain.exception.SolicitudDuplicadaException;
import org.ups.liberacionesobra.domain.exception.SolicitudNotFoundException;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Dada una excepción genérica no controlada, Cuando handleGeneric, Entonces responde 500 con código ERROR_INTERNO")
    void dadaExcepcionGenerica_cuandoHandleGeneric_entoncesResponde500() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new RuntimeException("fallo inesperado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("codigoError", "ERROR_INTERNO");
        assertThat(response.getBody()).containsEntry("mensaje", "Error interno del servidor");
    }

    @Test
    @DisplayName("Dada SolicitudNotFoundException, Cuando handleNotFound, Entonces responde 404")
    void dadaSolicitudNoEncontrada_cuandoHandleNotFound_entoncesResponde404() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new SolicitudNotFoundException(id));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("codigoError", "SOLICITUD_NO_ENCONTRADA");
    }

    @Test
    @DisplayName("Dada SolicitudDuplicadaException, Cuando handleDuplicado, Entonces responde 409 con id existente")
    void dadaSolicitudDuplicada_cuandoHandleDuplicado_entoncesResponde409() {
        UUID existenteId = UUID.randomUUID();
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicado(new SolicitudDuplicadaException(existenteId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("solicitudExistenteId", existenteId.toString());
    }

    @Test
    @DisplayName("Dado IllegalArgumentException, Cuando handleIllegalArg, Entonces responde 404 con código RECURSO_NO_ENCONTRADO")
    void dadoIllegalArgumentException_cuandoHandleIllegalArg_entoncesResponde404() {
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArg(new IllegalArgumentException("Frente no encontrado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("codigoError", "RECURSO_NO_ENCONTRADO");
    }

    @Test
    @DisplayName("Dados campos inválidos, Cuando handleValidacion, Entonces responde 400 con un error por campo")
    void dadosCamposInvalidos_cuandoHandleValidacion_entoncesResponde400() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "frenteId", "El campo frenteId es obligatorio"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("errores");
    }
}
