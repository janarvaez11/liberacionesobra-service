package org.ups.liberacionesobra.domain.repository;

import org.ups.liberacionesobra.domain.model.Actividad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para la consulta de actividades del catálogo. */
public interface ActividadRepository {

    /**
     * Busca una actividad por su identificador único.
     *
     * @param id UUID de la actividad
     * @return {@code Optional} con la actividad si existe, vacío en caso contrario
     */
    Optional<Actividad> buscarPorId(UUID id);

    /**
     * Devuelve todas las actividades asociadas al frente indicado.
     *
     * @param frenteId UUID del frente propietario
     * @return lista (posiblemente vacía) de actividades del frente
     */
    List<Actividad> listarPorFrente(UUID frenteId);
}
