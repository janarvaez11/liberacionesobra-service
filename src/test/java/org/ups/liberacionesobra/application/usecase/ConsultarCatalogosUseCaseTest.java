package org.ups.liberacionesobra.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.domain.repository.ActividadRepository;
import org.ups.liberacionesobra.domain.repository.FrenteRepository;
import org.ups.liberacionesobra.domain.repository.ProyectoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultarCatalogosUseCase — RF-001/RF-003 (catálogos de proyecto, frente y actividad)")
class ConsultarCatalogosUseCaseTest {

    @Mock private ProyectoRepository  proyectoRepo;
    @Mock private FrenteRepository    frenteRepo;
    @Mock private ActividadRepository actividadRepo;

    private ConsultarCatalogosUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarCatalogosUseCase(proyectoRepo, frenteRepo, actividadRepo);
    }

    @Test
    @DisplayName("Cuando listarProyectos, Entonces devuelve el listado del repositorio")
    void cuandoListarProyectos_entoncesDevuelveListado() {
        Proyecto proyecto = mock(Proyecto.class);
        when(proyectoRepo.listarTodos()).thenReturn(List.of(proyecto));

        List<Proyecto> resultado = useCase.listarProyectos();

        assertThat(resultado).containsExactly(proyecto);
    }

    @Test
    @DisplayName("Dado proyecto existente, Cuando listarFrentesPorProyecto, Entonces devuelve sus frentes")
    void dadoProyectoExistente_cuandoListarFrentes_entoncesDevuelveFrentes() {
        UUID proyectoId = UUID.randomUUID();
        Frente frente = mock(Frente.class);
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.of(mock(Proyecto.class)));
        when(frenteRepo.listarPorProyecto(proyectoId)).thenReturn(List.of(frente));

        List<Frente> resultado = useCase.listarFrentesPorProyecto(proyectoId);

        assertThat(resultado).containsExactly(frente);
    }

    @Test
    @DisplayName("Dado proyecto inexistente, Cuando listarFrentesPorProyecto, Entonces lanza IllegalArgumentException")
    void dadoProyectoInexistente_cuandoListarFrentes_entoncesLanzaExcepcion() {
        UUID proyectoId = UUID.randomUUID();
        when(proyectoRepo.buscarPorId(proyectoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.listarFrentesPorProyecto(proyectoId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(proyectoId.toString());
    }

    @Test
    @DisplayName("Dado frente existente, Cuando listarActividadesPorFrente, Entonces devuelve sus actividades")
    void dadoFrenteExistente_cuandoListarActividades_entoncesDevuelveActividades() {
        UUID frenteId = UUID.randomUUID();
        Actividad actividad = mock(Actividad.class);
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.of(mock(Frente.class)));
        when(actividadRepo.listarPorFrente(frenteId)).thenReturn(List.of(actividad));

        List<Actividad> resultado = useCase.listarActividadesPorFrente(frenteId);

        assertThat(resultado).containsExactly(actividad);
    }

    @Test
    @DisplayName("Dado frente inexistente, Cuando listarActividadesPorFrente, Entonces lanza IllegalArgumentException")
    void dadoFrenteInexistente_cuandoListarActividades_entoncesLanzaExcepcion() {
        UUID frenteId = UUID.randomUUID();
        when(frenteRepo.buscarPorId(frenteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.listarActividadesPorFrente(frenteId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(frenteId.toString());
    }
}
