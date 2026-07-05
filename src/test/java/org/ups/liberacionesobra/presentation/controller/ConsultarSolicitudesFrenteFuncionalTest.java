package org.ups.liberacionesobra.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("HU-2 · Consultar solicitudes por frente — pruebas funcionales")
class ConsultarSolicitudesFrenteFuncionalTest {

    @Autowired WebApplicationContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String PROYECTO_ID  = "11111111-0000-0000-0000-000000000001";
    private static final String FRENTE_ID    = "22222222-0000-0000-0000-000000000001";
    private static final String ACTIVIDAD_ID = "33333333-0000-0000-0000-000000000001";
    private static final String INSPECTOR_ID = "44444444-0000-0000-0000-000000000001";

    @Test
    @DisplayName("Dado solicitud creada, Cuando GET /solicitudes-inspeccion?frenteId=..., Entonces aparece en listado")
    void dadoSolicitudCreada_cuandoGetConFiltroFrente_entoncesAparece() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID);
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/solicitudes-inspeccion")
                        .param("frenteId", FRENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.solicitudes[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Dado sin solicitudes, Cuando GET /solicitudes-inspeccion, Entonces lista vacía con total=0")
    void dadoSinSolicitudes_cuandoGet_entoncesListaVacia() throws Exception {
        mockMvc.perform(get("/solicitudes-inspeccion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    @DisplayName("Dado solicitud creada, Cuando GET con estado=PENDIENTE, Entonces aparece en listado")
    void dadoSolicitudCreada_cuandoGetConEstado_entoncesAparece() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID);
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/solicitudes-inspeccion")
                        .param("frenteId", FRENTE_ID)
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @DisplayName("Dado solicitud creada, Cuando GET con proyectoId, Entonces aparece en listado")
    void dadoSolicitudCreada_cuandoGetConProyectoId_entoncesAparece() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID);
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/solicitudes-inspeccion")
                        .param("proyectoId", PROYECTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }
}
