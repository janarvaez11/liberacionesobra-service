package org.ups.liberacionesobra.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.domain.repository.ActividadRepository;
import org.ups.liberacionesobra.infrastructure.persistence.jpa.ActividadJpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.mapper.ActividadMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ActividadRepositoryImpl implements ActividadRepository {

    private final ActividadJpaRepository jpaRepository;
    private final ActividadMapper mapper;

    public ActividadRepositoryImpl(ActividadJpaRepository jpaRepository, ActividadMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public Optional<Actividad> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Actividad> listarPorFrente(UUID frenteId) {
        return jpaRepository.findByFrenteId(frenteId).stream().map(mapper::toDomain).toList();
    }
}
