package org.ups.liberacionesobra.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.ups.liberacionesobra.application.usecase.ConsultarCatalogosUseCase;
import org.ups.liberacionesobra.presentation.generated.api.CatalogoApi;
import org.ups.liberacionesobra.presentation.generated.model.ListaActividadesResponse;
import org.ups.liberacionesobra.presentation.generated.model.ListaFrentesResponse;
import org.ups.liberacionesobra.presentation.generated.model.ListaProyectosResponse;
import org.ups.liberacionesobra.presentation.mapper.CatalogoDtoMapper;

import java.util.UUID;

@RestController
public class CatalogoController implements CatalogoApi {

    private final ConsultarCatalogosUseCase catalogosUseCase;
    private final CatalogoDtoMapper mapper;

    public CatalogoController(ConsultarCatalogosUseCase catalogosUseCase, CatalogoDtoMapper mapper) {
        this.catalogosUseCase = catalogosUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<ListaProyectosResponse> listarProyectos() {
        ListaProyectosResponse resp = new ListaProyectosResponse();
        resp.setProyectos(catalogosUseCase.listarProyectos().stream().map(mapper::toResponse).toList());
        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<ListaFrentesResponse> listarFrentesPorProyecto(UUID proyectoId) {
        ListaFrentesResponse resp = new ListaFrentesResponse();
        resp.setFrentes(catalogosUseCase.listarFrentesPorProyecto(proyectoId).stream().map(mapper::toResponse).toList());
        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<ListaActividadesResponse> listarActividadesPorFrente(UUID frenteId) {
        ListaActividadesResponse resp = new ListaActividadesResponse();
        resp.setActividades(catalogosUseCase.listarActividadesPorFrente(frenteId).stream().map(mapper::toResponse).toList());
        return ResponseEntity.ok(resp);
    }
}
