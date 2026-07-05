package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.infrastructure.persistence.entity.SolicitudInspeccionJpaEntity;

@Component
public class SolicitudMapper {

    public SolicitudInspeccion toDomain(SolicitudInspeccionJpaEntity entity) {
        return SolicitudInspeccion.builder()
                .id(entity.getId())
                .proyectoId(entity.getProyectoId())
                .frenteId(entity.getFrenteId())
                .actividadId(entity.getActividadId())
                .inspectorId(entity.getInspectorId())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public SolicitudInspeccionJpaEntity toEntity(SolicitudInspeccion domain) {
        return SolicitudInspeccionJpaEntity.builder()
                .id(domain.getId())
                .proyectoId(domain.getProyectoId())
                .frenteId(domain.getFrenteId())
                .actividadId(domain.getActividadId())
                .inspectorId(domain.getInspectorId())
                .estado(domain.getEstado())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }
}
