package org.ups.liberacionesobra.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.entity.FrenteJpaEntity;

import java.util.List;
import java.util.UUID;

public interface FrenteJpaRepository extends JpaRepository<FrenteJpaEntity, UUID> {

    List<FrenteJpaEntity> findByProyectoId(UUID proyectoId);
}
