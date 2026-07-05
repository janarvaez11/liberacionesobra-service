package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ActividadJpaEntity;

@Component
public class ActividadMapper {

    public Actividad toDomain(ActividadJpaEntity entity) {
        return Actividad.builder()
                .id(entity.getId())
                .frenteId(entity.getFrenteId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public ActividadJpaEntity toEntity(Actividad domain) {
        return ActividadJpaEntity.builder()
                .id(domain.getId())
                .frenteId(domain.getFrenteId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .build();
    }
}
