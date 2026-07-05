package org.ups.liberacionesobra.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.notification.SolicitudNotificador;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Adaptador de infraestructura que implementa RF-005: mantiene un registro de
 * residentes suscritos (vía SSE) por frente y les hace push en tiempo real
 * cuando se crea una nueva solicitud de inspección para su frente.
 */
@Component
public class SseSolicitudNotificador implements SolicitudNotificador {

    private static final Logger log = LoggerFactory.getLogger(SseSolicitudNotificador.class);

    private static final long EMITTER_TIMEOUT_MS = 30 * 60 * 1000L;

    private final Map<UUID, List<SseEmitter>> suscriptoresPorFrente = new ConcurrentHashMap<>();

    /**
     * Registra un nuevo suscriptor (residente) para recibir eventos del frente indicado.
     *
     * @param frenteId UUID del frente a observar
     * @return el emitter SSE asociado a la suscripción
     */
    public SseEmitter suscribir(UUID frenteId) {
        SseEmitter emitter = crearEmitter();
        suscriptoresPorFrente
                .computeIfAbsent(frenteId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remover(frenteId, emitter));
        emitter.onTimeout(() -> remover(frenteId, emitter));
        emitter.onError(ex -> remover(frenteId, emitter));

        try {
            emitter.send(SseEmitter.event().name("conectado").data("suscrito"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        log.debug("Nuevo suscriptor registrado para frente={}", frenteId);
        return emitter;
    }

    @Override
    public void notificar(SolicitudInspeccion solicitud) {
        List<SseEmitter> emitters = suscriptoresPorFrente.get(solicitud.getFrenteId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : List.copyOf(emitters)) {
            try {
                emitter.send(SseEmitter.event()
                        .name("nueva-solicitud")
                        .data(solicitud));
            } catch (IOException e) {
                emitter.completeWithError(e);
                remover(solicitud.getFrenteId(), emitter);
            }
        }
        log.info("Solicitud {} notificada a {} suscriptor(es) del frente {}",
                solicitud.getId(), emitters.size(), solicitud.getFrenteId());
    }

    int contarSuscriptores(UUID frenteId) {
        List<SseEmitter> emitters = suscriptoresPorFrente.get(frenteId);
        return emitters == null ? 0 : emitters.size();
    }

    protected SseEmitter crearEmitter() {
        return new SseEmitter(EMITTER_TIMEOUT_MS);
    }

    private void remover(UUID frenteId, SseEmitter emitter) {
        List<SseEmitter> emitters = suscriptoresPorFrente.get(frenteId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
