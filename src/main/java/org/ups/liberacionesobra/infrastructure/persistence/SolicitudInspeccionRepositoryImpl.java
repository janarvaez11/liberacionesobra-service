package org.ups.liberacionesobra.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.repository.SolicitudInspeccionRepository;
import org.ups.liberacionesobra.infrastructure.persistence.jpa.SolicitudInspeccionJpaRepository;
import org.ups.liberacionesobra.infrastructure.persistence.mapper.SolicitudMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SolicitudInspeccionRepositoryImpl implements SolicitudInspeccionRepository {

    private final SolicitudInspeccionJpaRepository jpaRepository;
    private final SolicitudMapper mapper;

    public SolicitudInspeccionRepositoryImpl(SolicitudInspeccionJpaRepository jpaRepository,
                                             SolicitudMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public SolicitudInspeccion guardar(SolicitudInspeccion solicitud) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(solicitud)));
    }

    @Override
    public Optional<SolicitudInspeccion> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existePendientePara(UUID proyectoId, UUID frenteId, UUID actividadId) {
        return jpaRepository.existsByProyectoIdAndFrenteIdAndActividadIdAndEstado(
                proyectoId, frenteId, actividadId, EstadoSolicitud.PENDIENTE);
    }

    @Override
    public List<SolicitudInspeccion> buscarPorFiltros(UUID frenteId, UUID proyectoId,
                                                       EstadoSolicitud estado) {
        if (frenteId != null && estado != null) {
            return jpaRepository.findByFrenteIdAndEstado(frenteId, estado)
                    .stream().map(mapper::toDomain).toList();
        }
        if (frenteId != null) {
            return jpaRepository.findByFrenteId(frenteId)
                    .stream().map(mapper::toDomain).toList();
        }
        if (proyectoId != null) {
            return jpaRepository.findByProyectoId(proyectoId)
                    .stream().map(mapper::toDomain).toList();
        }
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
