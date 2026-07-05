package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ProyectoJpaEntity;

@Component
public class ProyectoMapper {

    public Proyecto toDomain(ProyectoJpaEntity entity) {
        return Proyecto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public ProyectoJpaEntity toEntity(Proyecto domain) {
        return ProyectoJpaEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .build();
    }
}
