package org.ups.liberacionesobra.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.infrastructure.persistence.entity.SolicitudInspeccionJpaEntity;

import java.util.List;
import java.util.UUID;

public interface SolicitudInspeccionJpaRepository extends JpaRepository<SolicitudInspeccionJpaEntity, UUID> {

    boolean existsByProyectoIdAndFrenteIdAndActividadIdAndEstado(
            UUID proyectoId, UUID frenteId, UUID actividadId, EstadoSolicitud estado);

    List<SolicitudInspeccionJpaEntity> findByFrenteIdAndEstado(UUID frenteId, EstadoSolicitud estado);

    List<SolicitudInspeccionJpaEntity> findByFrenteId(UUID frenteId);

    List<SolicitudInspeccionJpaEntity> findByProyectoId(UUID proyectoId);
}
