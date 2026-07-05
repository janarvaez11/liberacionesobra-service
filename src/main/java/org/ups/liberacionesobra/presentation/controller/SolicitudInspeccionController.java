package org.ups.liberacionesobra.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.ups.liberacionesobra.application.usecase.ConsultarSolicitudesFrenteUseCase;
import org.ups.liberacionesobra.application.usecase.CrearSolicitudInspeccionUseCase;
import org.ups.liberacionesobra.application.usecase.ObtenerSolicitudInspeccionUseCase;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.presentation.generated.api.SolicitudesApi;
import org.ups.liberacionesobra.presentation.generated.model.CrearSolicitudRequest;
import org.ups.liberacionesobra.presentation.generated.model.EstadoSolicitud;
import org.ups.liberacionesobra.presentation.generated.model.ListaSolicitudesResponse;
import org.ups.liberacionesobra.presentation.generated.model.SolicitudInspeccionResponse;
import org.ups.liberacionesobra.presentation.mapper.SolicitudDtoMapper;

import java.util.List;
import java.util.UUID;

@RestController
public class SolicitudInspeccionController implements SolicitudesApi {

    private static final Logger log = LoggerFactory.getLogger(SolicitudInspeccionController.class);

    private final CrearSolicitudInspeccionUseCase  crearUseCase;
    private final ObtenerSolicitudInspeccionUseCase obtenerUseCase;
    private final ConsultarSolicitudesFrenteUseCase consultarUseCase;
    private final SolicitudDtoMapper mapper;

    public SolicitudInspeccionController(CrearSolicitudInspeccionUseCase crearUseCase,
                                         ObtenerSolicitudInspeccionUseCase obtenerUseCase,
                                         ConsultarSolicitudesFrenteUseCase consultarUseCase,
                                         SolicitudDtoMapper mapper) {
        this.crearUseCase    = crearUseCase;
        this.obtenerUseCase  = obtenerUseCase;
        this.consultarUseCase = consultarUseCase;
        this.mapper          = mapper;
    }

    @Override
    public ResponseEntity<SolicitudInspeccionResponse> crearSolicitudInspeccion(
            CrearSolicitudRequest request) {
        log.debug("POST /solicitudes-inspeccion proyecto={} frente={} actividad={}",
                request.getProyectoId(), request.getFrenteId(), request.getActividadId());
        SolicitudInspeccion creada = crearUseCase.ejecutar(
                mapper.proyectoId(request), mapper.frenteId(request),
                mapper.actividadId(request), mapper.inspectorId(request),
                mapper.forzar(request));
        return ResponseEntity.status(201).body(mapper.toResponse(creada));
    }

    @Override
    public ResponseEntity<SolicitudInspeccionResponse> obtenerSolicitudInspeccion(UUID id) {
        log.debug("GET /solicitudes-inspeccion/{}", id);
        return ResponseEntity.ok(mapper.toResponse(obtenerUseCase.ejecutar(id)));
    }

    @Override
    public ResponseEntity<ListaSolicitudesResponse> listarSolicitudesInspeccion(
            UUID frenteId, UUID proyectoId, EstadoSolicitud estado) {
        log.debug("GET /solicitudes-inspeccion frenteId={} proyectoId={} estado={}", frenteId, proyectoId, estado);
        org.ups.liberacionesobra.domain.model.EstadoSolicitud estadoEnum = estado != null
                ? org.ups.liberacionesobra.domain.model.EstadoSolicitud.valueOf(estado.name())
                : null;
        List<SolicitudInspeccionResponse> items = consultarUseCase
                .ejecutar(frenteId, proyectoId, estadoEnum)
                .stream().map(mapper::toResponse).toList();
        ListaSolicitudesResponse resp = new ListaSolicitudesResponse();
        resp.setSolicitudes(items);
        resp.setTotal(items.size());
        return ResponseEntity.ok(resp);
    }
}
