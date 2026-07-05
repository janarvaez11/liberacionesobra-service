package org.ups.liberacionesobra.domain.repository;

import org.ups.liberacionesobra.domain.model.Proyecto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para la consulta de proyectos del catálogo. */
public interface ProyectoRepository {

    /**
     * Busca un proyecto por su identificador único.
     *
     * @param id UUID del proyecto
     * @return {@code Optional} con el proyecto si existe, vacío en caso contrario
     */
    Optional<Proyecto> buscarPorId(UUID id);

    /**
     * Devuelve todos los proyectos disponibles en el catálogo.
     *
     * @return lista (posiblemente vacía) de proyectos
     */
    List<Proyecto> listarTodos();
}
