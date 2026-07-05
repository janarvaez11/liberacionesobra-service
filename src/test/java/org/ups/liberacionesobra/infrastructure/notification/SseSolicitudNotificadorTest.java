package org.ups.liberacionesobra.infrastructure.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RF-005: verifica que el adaptador SSE registra suscriptores por frente y les
 * hace push real cuando se notifica una nueva solicitud.
 */
@DisplayName("SseSolicitudNotificador — registro de suscriptores y push (RF-005)")
class SseSolicitudNotificadorTest {

    private SseEmitter emitterSpy;
    private SseSolicitudNotificador notificador;

    @BeforeEach
    void setUp() {
        emitterSpy = spy(new SseEmitter());
        notificador = new SseSolicitudNotificador() {
            @Override
            protected SseEmitter crearEmitter() {
                return emitterSpy;
            }
        };
    }

    private SolicitudInspeccion solicitudPara(UUID frenteId) {
        return SolicitudInspeccion.builder()
                .id(UUID.randomUUID())
                .proyectoId(UUID.randomUUID())
                .frenteId(frenteId)
                .actividadId(UUID.randomUUID())
                .inspectorId(UUID.randomUUID())
                .estado(EstadoSolicitud.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Dado un residente suscrito, Cuando se notifica una solicitud de su frente, Entonces recibe el push con los datos")
    void dadoSuscriptor_cuandoNotificarMismoFrente_entoncesRecibePush() throws Exception {
        UUID frenteId = UUID.randomUUID();

        SseEmitter devuelto = notificador.suscribir(frenteId);
        assertThat(devuelto).isSameAs(emitterSpy);
        assertThat(notificador.contarSuscriptores(frenteId)).isEqualTo(1);

        clearInvocations(emitterSpy);
        SolicitudInspeccion solicitud = solicitudPara(frenteId);
        notificador.notificar(solicitud);

        var captor = org.mockito.ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
        verify(emitterSpy).send(captor.capture());

        Set<ResponseBodyEmitter.DataWithMediaType> enviado = captor.getValue().build();
        assertThat(enviado).anyMatch(d -> d.getData() == solicitud);
    }

    @Test
    @DisplayName("Dado ningún suscriptor, Cuando se notifica una solicitud, Entonces no falla y no envía nada")
    void dadoSinSuscriptores_cuandoNotificar_entoncesNoFalla() {
        SolicitudInspeccion solicitud = solicitudPara(UUID.randomUUID());

        notificador.notificar(solicitud);

        verifyNoInteractions(emitterSpy);
    }

    @Test
    @DisplayName("Dado suscriptor de otro frente, Cuando se notifica, Entonces no recibe el push")
    void dadoSuscriptorDeOtroFrente_cuandoNotificar_entoncesNoRecibe() throws Exception {
        UUID frenteSuscrito = UUID.randomUUID();
        UUID frenteNotificado = UUID.randomUUID();
        notificador.suscribir(frenteSuscrito);
        clearInvocations(emitterSpy);

        notificador.notificar(solicitudPara(frenteNotificado));

        verify(emitterSpy, never()).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("Dado un suscriptor, Cuando su emitter dispara el callback de finalización, Entonces se elimina del registro")
    void dadoSuscriptor_cuandoEmitterCompleta_entoncesSeElimina() {
        UUID frenteId = UUID.randomUUID();
        notificador.suscribir(frenteId);
        assertThat(notificador.contarSuscriptores(frenteId)).isEqualTo(1);

        var callback = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        verify(emitterSpy).onCompletion(callback.capture());
        callback.getValue().run();

        assertThat(notificador.contarSuscriptores(frenteId)).isEqualTo(0);
    }
}
