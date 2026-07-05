package org.ups.liberacionesobra.domain.repository;

import org.ups.liberacionesobra.domain.model.Frente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para la consulta de frentes del catálogo. */
public interface FrenteRepository {

    /**
     * Busca un frente por su identificador único.
     *
     * @param id UUID del frente
     * @return {@code Optional} con el frente si existe, vacío en caso contrario
     */
    Optional<Frente> buscarPorId(UUID id);

    /**
     * Devuelve todos los frentes pertenecientes al proyecto indicado.
     *
     * @param proyectoId UUID del proyecto propietario
     * @return lista (posiblemente vacía) de frentes del proyecto
     */
    List<Frente> listarPorProyecto(UUID proyectoId);
}
