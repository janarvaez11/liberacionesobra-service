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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearSolicitudInspeccionUseCase")
class CrearSolicitudInspeccionUseCaseTest {

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

    @Test
    @DisplayName("Dado datos válidos, Cuando se crea la solicitud, Entonces estado es PENDIENTE y fechaCreacion está asignada")
    void dadoDatosValidos_cuandoSeCreaSolicitud_entoncesEstadoPendienteYFechaAsignada() {
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Proyecto.class)));
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Frente.class)));
        when(actividadRepo.buscarPorId(actividadId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Actividad.class)));
        when(solicitudRepo.existePendientePara(proyectoId, frenteId, actividadId)).thenReturn(false);
        when(solicitudRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitudInspeccion resultado = useCase.ejecutar(proyectoId, frenteId, actividadId, inspectorId, false);

        assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(resultado.getFechaCreacion()).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        verify(notificador).notificar(resultado);
    }

    @Test
    @DisplayName("Dado duplicado activo, Cuando forzar=false, Entonces lanza SolicitudDuplicadaException")
    void dadoDuplicadoActivo_cuandoForzarFalse_entoncesLanzaExcepcion() {
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Proyecto.class)));
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Frente.class)));
        when(actividadRepo.buscarPorId(actividadId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Actividad.class)));
        when(solicitudRepo.existePendientePara(proyectoId, frenteId, actividadId)).thenReturn(true);
        when(solicitudRepo.buscarPorFiltros(frenteId, proyectoId, EstadoSolicitud.PENDIENTE))
                .thenReturn(java.util.List.of(SolicitudInspeccion.builder()
                        .id(UUID.randomUUID()).proyectoId(proyectoId).frenteId(frenteId)
                        .actividadId(actividadId).inspectorId(inspectorId)
                        .estado(EstadoSolicitud.PENDIENTE)
                        .fechaCreacion(java.time.LocalDateTime.now()).build()));

        assertThatThrownBy(() -> useCase.ejecutar(proyectoId, frenteId, actividadId, inspectorId, false))
                .isInstanceOf(SolicitudDuplicadaException.class);
        verifyNoInteractions(notificador);
    }

    @Test
    @DisplayName("Dado duplicado activo, Cuando forzar=true, Entonces crea solicitud sin excepción")
    void dadoDuplicadoActivo_cuandoForzarTrue_entoncesCreaSinExcepcion() {
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Proyecto.class)));
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Frente.class)));
        when(actividadRepo.buscarPorId(actividadId)).thenReturn(Optional.of(mock(org.ups.liberacionesobra.domain.model.Actividad.class)));
        when(solicitudRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitudInspeccion resultado = useCase.ejecutar(proyectoId, frenteId, actividadId, inspectorId, true);

        assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        verify(solicitudRepo).guardar(any());
        verify(notificador).notificar(resultado);
    }
}
