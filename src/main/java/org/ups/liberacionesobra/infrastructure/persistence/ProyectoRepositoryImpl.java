package org.ups.liberacionesobra.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.domain.repository.ProyectoRepository;
import org.ups.liberacionesobra.infrastructure.persistence.jpa.ProyectoJpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.mapper.ProyectoMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProyectoRepositoryImpl implements ProyectoRepository {

    private final ProyectoJpaRepository jpaRepository;
    private final ProyectoMapper mapper;

    public ProyectoRepositoryImpl(ProyectoJpaRepository jpaRepository, ProyectoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public Optional<Proyecto> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Proyecto> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
