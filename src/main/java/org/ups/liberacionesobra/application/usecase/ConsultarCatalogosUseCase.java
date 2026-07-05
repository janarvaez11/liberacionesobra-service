package org.ups.liberacionesobra.application.usecase;

import org.springframework.stereotype.Service;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.domain.repository.ActividadRepository;
import org.ups.liberacionesobra.domain.repository.FrenteRepository;
import org.ups.liberacionesobra.domain.repository.ProyectoRepository;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: consultar el catálogo de proyectos, frentes y actividades.
 * Valida que las entidades padre existan antes de devolver sus hijos.
 */
@Service
public class ConsultarCatalogosUseCase {

    private final ProyectoRepository  proyectoRepo;
    private final FrenteRepository    frenteRepo;
    private final ActividadRepository actividadRepo;

    public ConsultarCatalogosUseCase(ProyectoRepository proyectoRepo,
                                     FrenteRepository frenteRepo,
                                     ActividadRepository actividadRepo) {
        this.proyectoRepo  = proyectoRepo;
        this.frenteRepo    = frenteRepo;
        this.actividadRepo = actividadRepo;
    }

    /**
     * Devuelve todos los proyectos del catálogo.
     *
     * @return lista (posiblemente vacía) de proyectos
     */
    public List<Proyecto> listarProyectos() {
        return proyectoRepo.listarTodos();
    }

    /**
     * Devuelve los frentes del proyecto indicado.
     *
     * @param proyectoId UUID del proyecto
     * @return lista (posiblemente vacía) de frentes del proyecto
     * @throws IllegalArgumentException si el proyecto no existe en el catálogo
     */
    public List<Frente> listarFrentesPorProyecto(UUID proyectoId) {
        proyectoRepo.buscarPorId(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + proyectoId));
        return frenteRepo.listarPorProyecto(proyectoId);
    }

    /**
     * Devuelve las actividades del frente indicado.
     *
     * @param frenteId UUID del frente
     * @return lista (posiblemente vacía) de actividades del frente
     * @throws IllegalArgumentException si el frente no existe en el catálogo
     */
    public List<Actividad> listarActividadesPorFrente(UUID frenteId) {
        frenteRepo.buscarPorId(frenteId)
                .orElseThrow(() -> new IllegalArgumentException("Frente no encontrado: " + frenteId));
        return actividadRepo.listarPorFrente(frenteId);
    }
}
