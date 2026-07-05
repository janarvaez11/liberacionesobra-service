package org.ups.liberacionesobra.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ActividadJpaEntity;

import java.util.List;
import java.util.UUID;

public interface ActividadJpaRepository extends JpaRepository<ActividadJpaEntity, UUID> {

    List<ActividadJpaEntity> findByFrenteId(UUID frenteId);
}
