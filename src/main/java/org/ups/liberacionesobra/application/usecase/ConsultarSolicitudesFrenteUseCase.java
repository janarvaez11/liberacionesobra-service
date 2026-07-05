package org.ups.liberacionesobra.application.usecase;

import org.springframework.stereotype.Service;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.repository.SolicitudInspeccionRepository;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: consultar el listado de solicitudes de inspección con filtros opcionales
 * de frente, proyecto y estado.
 */
@Service
public class ConsultarSolicitudesFrenteUseCase {

    private final SolicitudInspeccionRepository solicitudRepo;

    public ConsultarSolicitudesFrenteUseCase(SolicitudInspeccionRepository solicitudRepo) {
        this.solicitudRepo = solicitudRepo;
    }

    /**
     * Devuelve las solicitudes que coinciden con los filtros indicados.
     * Los parámetros {@code null} se omiten de la búsqueda.
     *
     * @param frenteId   UUID del frente (puede ser {@code null})
     * @param proyectoId UUID del proyecto (puede ser {@code null})
     * @param estado     estado de la solicitud (puede ser {@code null})
     * @return lista (posiblemente vacía) de solicitudes que cumplen los filtros
     */
    public List<SolicitudInspeccion> ejecutar(UUID frenteId, UUID proyectoId, EstadoSolicitud estado) {
        return solicitudRepo.buscarPorFiltros(frenteId, proyectoId, estado);
    }
}
