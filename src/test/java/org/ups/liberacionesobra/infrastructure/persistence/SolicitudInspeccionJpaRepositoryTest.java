package org.ups.liberacionesobra.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.ups.liberacionesobra.infrastructure.persistence.entity.SolicitudInspeccionJpaEntity;
import org.ups.liberacionesobra.infrastructure.persistence.jpa.SolicitudInspeccionJpaRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("SolicitudInspeccionJpaRepository")
class SolicitudInspeccionJpaRepositoryTest {

    @Autowired
    private SolicitudInspeccionJpaRepository repository;

    @Test
    @DisplayName("Dado una entidad de solicitud, Cuando se guarda y recupera por ID, Entonces los campos persisten correctamente")
    void dadoEntidadSolicitud_cuandoGuardaYRecuperaPorId_entoncesFieldsPersisten() {
        UUID id = UUID.randomUUID();
        UUID proyectoId  = UUID.fromString("11111111-0000-0000-0000-000000000001");
        UUID frenteId    = UUID.fromString("22222222-0000-0000-0000-000000000001");
        UUID actividadId = UUID.fromString("33333333-0000-0000-0000-000000000001");
        UUID inspectorId = UUID.fromString("44444444-0000-0000-0000-000000000001");

        SolicitudInspeccionJpaEntity entity = new SolicitudInspeccionJpaEntity(
                id, proyectoId, frenteId, actividadId, inspectorId,
                org.ups.liberacionesobra.domain.model.EstadoSolicitud.PENDIENTE,
                java.time.LocalDateTime.now()
        );

        repository.save(entity);

        Optional<SolicitudInspeccionJpaEntity> encontrada = repository.findById(id);

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getProyectoId()).isEqualTo(proyectoId);
        assertThat(encontrada.get().getFrenteId()).isEqualTo(frenteId);
        assertThat(encontrada.get().getActividadId()).isEqualTo(actividadId);
        assertThat(encontrada.get().getEstado()).isEqualTo(org.ups.liberacionesobra.domain.model.EstadoSolicitud.PENDIENTE);
        assertThat(encontrada.get().getFechaCreacion()).isNotNull();
    }
}
