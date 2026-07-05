package org.ups.liberacionesobra.domain.service;

import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de dominio puro: construye entidades SolicitudInspeccion.
 * No inyecta repositorios ni tiene dependencias de framework.
 */
public class SolicitudDomainService {

    public SolicitudInspeccion crearSolicitud(UUID proyectoId, UUID frenteId,
                                              UUID actividadId, UUID inspectorId) {
        return SolicitudInspeccion.builder()
                .id(UUID.randomUUID())
                .proyectoId(proyectoId)
                .frenteId(frenteId)
                .actividadId(actividadId)
                .inspectorId(inspectorId)
                .estado(EstadoSolicitud.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}
