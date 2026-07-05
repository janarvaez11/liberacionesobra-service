package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.infrastructure.persistence.entity.FrenteJpaEntity;

@Component
public class FrenteMapper {

    public Frente toDomain(FrenteJpaEntity entity) {
        return Frente.builder()
                .id(entity.getId())
                .proyectoId(entity.getProyectoId())
                .nombre(entity.getNombre())
                .residenteId(entity.getResidenteId())
                .build();
    }

    public FrenteJpaEntity toEntity(Frente domain) {
        return FrenteJpaEntity.builder()
                .id(domain.getId())
                .proyectoId(domain.getProyectoId())
                .nombre(domain.getNombre())
                .residenteId(domain.getResidenteId())
                .build();
    }
}
