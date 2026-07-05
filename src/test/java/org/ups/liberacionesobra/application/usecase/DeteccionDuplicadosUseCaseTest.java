package org.ups.liberacionesobra.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.liberacionesobra.domain.exception.SolicitudDuplicadaException;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.notification.SolicitudNotificador;
import org.ups.liberacionesobra.domain.repository.ActividadRepository;
import org.ups.liberacionesobra.domain.repository.FrenteRepository;
import org.ups.liberacionesobra.domain.repository.ProyectoRepository;
import org.ups.liberacionesobra.domain.repository.SolicitudInspeccionRepository;
import org.ups.liberacionesobra.domain.service.SolicitudDomainService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Detección de duplicados — CrearSolicitudInspeccionUseCase")
class DeteccionDuplicadosUseCaseTest {

    @Mock private SolicitudInspeccionRepository solicitudRepo;
    @Mock private ProyectoRepository            proyectoRepo;
    @Mock private FrenteRepository              frenteRepo;
    @Mock private ActividadRepository           actividadRepo;
    @Mock private SolicitudNotificador          notificador;

    private CrearSolicitudInspeccionUseCase useCase;

    private final UUID proyectoId  = UUID.fromString("11111111-0000-0000-0000-000000000001");
    private final UUID frenteId    = UUID.fromString("22222222-0000-0000-0000-000000000001");
    private final UUID actividadId = UUID.fromString("33333333-0000-0000-0000-000000000001");
    private final UUID inspectorId = UUID.fromString("44444444-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new CrearSolicitudInspeccionUseCase(
                solicitudRepo, proyectoRepo, frenteRepo, actividadRepo,
                new SolicitudDomainService(), notificador);
    }

    private void stubCatalogosOk() {
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Proyecto.class)));
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Frente.class)));
        when(actividadRepo.buscarPorId(actividadId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Actividad.class)));
    }

    @Test
    @DisplayName("Dado solicitud duplicada pendiente, Cuando forzar=false, Entonces lanza excepción con id de la existente")
    void dadoDuplicado_cuandoForzarFalse_entoncesExcepcionConIdExistente() {
        UUID existenteId = UUID.randomUUID();
        stubCatalogosOk();
        when(solicitudRepo.existePendientePara(proyectoId, frenteId, actividadId)).thenReturn(true);
        when(solicitudRepo.buscarPorFiltros(frenteId, proyectoId, EstadoSolicitud.PENDIENTE))
                .thenReturn(List.of(SolicitudInspeccion.builder()
                        .id(existenteId).proyectoId(proyectoId).frenteId(frenteId)
                        .actividadId(actividadId).inspectorId(inspectorId)
                        .estado(EstadoSolicitud.PENDIENTE)
                        .fechaCreacion(LocalDateTime.now()).build()));

        assertThatThrownBy(() -> useCase.ejecutar(proyectoId, frenteId, actividadId, inspectorId, false))
                .isInstanceOf(SolicitudDuplicadaException.class)
                .satisfies(ex -> {
                    SolicitudDuplicadaException sde = (SolicitudDuplicadaException) ex;
                    assertThat(sde.getSolicitudExistenteId()).isEqualTo(existenteId);
                });
    }

    @Test
    @DisplayName("Dado solicitud duplicada, Cuando forzar=true, Entonces crea nueva solicitud sin error")
    void dadoDuplicado_cuandoForzarTrue_entoncesCreaSinError() {
        stubCatalogosOk();
        when(solicitudRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitudInspeccion resultado = useCase.ejecutar(proyectoId, frenteId, actividadId, inspectorId, true);

        assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
    }
}
