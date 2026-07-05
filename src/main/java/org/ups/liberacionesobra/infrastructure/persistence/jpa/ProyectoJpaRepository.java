package org.ups.liberacionesobra.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ProyectoJpaEntity;

import java.util.UUID;

public interface ProyectoJpaRepository extends JpaRepository<ProyectoJpaEntity, UUID> {
}
