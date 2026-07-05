package org.ups.liberacionesobra.domain.repository;

import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de solicitudes de inspección.
 * Las implementaciones concretas viven en la capa de infraestructura.
 */
public interface SolicitudInspeccionRepository {

    /**
     * Persiste una solicitud de inspección (creación o actualización).
     *
     * @param solicitud entidad de dominio a guardar
     * @return la entidad guardada con los valores asignados por la persistencia (p.ej. fechas)
     */
    SolicitudInspeccion guardar(SolicitudInspeccion solicitud);

    /**
     * Busca una solicitud por su identificador único.
     *
     * @param id identificador UUID de la solicitud
     * @return {@code Optional} con la solicitud si existe, vacío en caso contrario
     */
    Optional<SolicitudInspeccion> buscarPorId(UUID id);

    /**
     * Indica si existe al menos una solicitud en estado PENDIENTE para la combinación
     * proyecto-frente-actividad dada.
     *
     * @param proyectoId  UUID del proyecto
     * @param frenteId    UUID del frente
     * @param actividadId UUID de la actividad
     * @return {@code true} si existe un duplicado activo
     */
    boolean existePendientePara(UUID proyectoId, UUID frenteId, UUID actividadId);

    /**
     * Devuelve las solicitudes que coinciden con los filtros opcionales suministrados.
     * Los parámetros {@code null} se ignoran en la búsqueda.
     *
     * @param frenteId   UUID del frente (puede ser {@code null})
     * @param proyectoId UUID del proyecto (puede ser {@code null})
     * @param estado     estado de la solicitud (puede ser {@code null})
     * @return lista (posiblemente vacía) de solicitudes que cumplen los filtros
     */
    List<SolicitudInspeccion> buscarPorFiltros(UUID frenteId, UUID proyectoId, EstadoSolicitud estado);
}
