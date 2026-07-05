package org.ups.liberacionesobra.presentation.mapper;

import org.springframework.stereotype.Component;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.presentation.generated.model.CrearSolicitudRequest;
import org.ups.liberacionesobra.presentation.generated.model.EstadoSolicitud;
import org.ups.liberacionesobra.presentation.generated.model.SolicitudInspeccionResponse;

import java.util.UUID;

@Component
public class SolicitudDtoMapper {

    public SolicitudInspeccionResponse toResponse(SolicitudInspeccion domain) {
        SolicitudInspeccionResponse resp = new SolicitudInspeccionResponse();
        resp.setId(domain.getId());
        resp.setProyectoId(domain.getProyectoId());
        resp.setFrenteId(domain.getFrenteId());
        resp.setActividadId(domain.getActividadId());
        resp.setInspectorId(domain.getInspectorId());
        resp.setEstado(EstadoSolicitud.valueOf(domain.getEstado().name()));
        resp.setFechaCreacion(domain.getFechaCreacion().atOffset(java.time.ZoneOffset.UTC));
        return resp;
    }

    public UUID proyectoId(CrearSolicitudRequest req)  { return req.getProyectoId(); }
    public UUID frenteId(CrearSolicitudRequest req)    { return req.getFrenteId(); }
    public UUID actividadId(CrearSolicitudRequest req) { return req.getActividadId(); }
    public UUID inspectorId(CrearSolicitudRequest req) { return req.getInspectorId(); }
    public boolean forzar(CrearSolicitudRequest req)   {
        return Boolean.TRUE.equals(req.getForzar());
    }
}
