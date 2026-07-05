package org.ups.liberacionesobra.presentation.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.presentation.generated.model.ActividadResponse;
import org.ups.liberacionesobra.presentation.generated.model.FrenteResponse;
import org.ups.liberacionesobra.presentation.generated.model.ProyectoResponse;

@Component
public class CatalogoDtoMapper {

    public ProyectoResponse toResponse(Proyecto p) {
        ProyectoResponse r = new ProyectoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        return r;
    }

    public FrenteResponse toResponse(Frente f) {
        FrenteResponse r = new FrenteResponse();
        r.setId(f.getId());
        r.setProyectoId(f.getProyectoId());
        r.setNombre(f.getNombre());
        r.setResidenteId(f.getResidenteId());
        return r;
    }

    public ActividadResponse toResponse(Actividad a) {
        ActividadResponse r = new ActividadResponse();
        r.setId(a.getId());
        r.setFrenteId(a.getFrenteId());
        r.setNombre(a.getNombre());
        r.setDescripcion(a.getDescripcion());
        return r;
    }
}
