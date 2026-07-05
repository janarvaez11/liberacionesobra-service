package org.ups.liberacionesobra.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.domain.repository.FrenteRepository;
import org.ups.liberacionesobra.infrastructure.persistence.jpa.FrenteJpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.mapper.FrenteMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FrenteRepositoryImpl implements FrenteRepository {

    private final FrenteJpaRepository jpaRepository;
    private final FrenteMapper mapper;

    public FrenteRepositoryImpl(FrenteJpaRepository jpaRepository, FrenteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public Optional<Frente> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Frente> listarPorProyecto(UUID proyectoId) {
        return jpaRepository.findByProyectoId(proyectoId).stream().map(mapper::toDomain).toList();
    }
}
