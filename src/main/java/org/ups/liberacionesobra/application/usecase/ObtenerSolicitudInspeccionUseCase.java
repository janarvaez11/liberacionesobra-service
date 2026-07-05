package org.ups.liberacionesobra.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ups.liberacionesobra.domain.exception.SolicitudNotFoundException;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.repository.SolicitudInspeccionRepository;

import java.util.UUID;

/**
 * Caso de uso: recuperar una solicitud de inspección existente por su identificador único.
 * Lanza {@link org.ups.liberacionesobra.domain.exception.SolicitudNotFoundException}
 * si no existe ninguna solicitud con el id proporcionado.
 */
@Service
public class ObtenerSolicitudInspeccionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ObtenerSolicitudInspeccionUseCase.class);

    private final SolicitudInspeccionRepository repository;

    public ObtenerSolicitudInspeccionUseCase(SolicitudInspeccionRepository repository) {
        this.repository = repository;
    }

    /**
     * Devuelve la solicitud con el identificador dado.
     *
     * @param id UUID de la solicitud
     * @return la solicitud de inspección encontrada
     * @throws org.ups.liberacionesobra.domain.exception.SolicitudNotFoundException si no existe
     */
    public SolicitudInspeccion ejecutar(UUID id) {
        return repository.buscarPorId(id).orElseThrow(() -> {
            log.warn("Solicitud no encontrada con id={}", id);
            return new SolicitudNotFoundException(id);
        });
    }
}
