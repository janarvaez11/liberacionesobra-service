package org.ups.liberacionesobra.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ups.liberacionesobra.infrastructure.notification.SseSolicitudNotificador;

import java.util.UUID;

/**
 * RF-005: expone el mecanismo de suscripción en tiempo real para que el residente
 * reciba automáticamente las nuevas solicitudes de inspección de su frente,
 * sin necesidad de recargar la pantalla ni hacer polling manual.
 */
@RestController
public class SolicitudEventosController {

    private static final Logger log = LoggerFactory.getLogger(SolicitudEventosController.class);

    private final SseSolicitudNotificador notificador;

    public SolicitudEventosController(SseSolicitudNotificador notificador) {
        this.notificador = notificador;
    }

    @Operation(
            summary = "Suscribirse a actualizaciones en tiempo real de un frente",
            description = "Abre un stream Server-Sent Events (SSE). Mientras la conexión esté "
                    + "abierta, el residente recibe automáticamente un evento 'nueva-solicitud' "
                    + "cada vez que se crea una solicitud de inspección para este frente, "
                    + "sin necesidad de recargar la pantalla ni de sondear el servidor.",
            responses = @ApiResponse(responseCode = "200", description = "Stream SSE abierto")
    )
    @GetMapping(value = "/frentes/{frenteId}/solicitudes-inspeccion/eventos",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter suscribirseAFrente(@PathVariable UUID frenteId) {
        log.debug("Nueva suscripción SSE al frente={}", frenteId);
        return notificador.suscribir(frenteId);
    }
}
