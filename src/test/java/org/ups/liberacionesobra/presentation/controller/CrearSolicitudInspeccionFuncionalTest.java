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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("HU-1 · Crear solicitud de inspección — pruebas funcionales")
class CrearSolicitudInspeccionFuncionalTest {

    @Autowired WebApplicationContext context;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String PROYECTO_ID   = "11111111-0000-0000-0000-000000000001";
    private static final String FRENTE_ID     = "22222222-0000-0000-0000-000000000001";
    private static final String ACTIVIDAD_ID  = "33333333-0000-0000-0000-000000000001";
    private static final String INSPECTOR_ID  = "44444444-0000-0000-0000-000000000001";

    @Test
    @DisplayName("Dado datos válidos, Cuando POST /solicitudes-inspeccion, Entonces 201 con estado PENDIENTE")
    void dadoDatosValidos_cuandoPost_entonces201ConEstadoPendiente() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId",  PROYECTO_ID,
                "frenteId",    FRENTE_ID,
                "actividadId", ACTIVIDAD_ID,
                "inspectorId", INSPECTOR_ID
        );

        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.fechaCreacion").isNotEmpty());
    }

    @Test
    @DisplayName("Dado proyecto inexistente, Cuando POST, Entonces 404")
    void dadoProyectoInexistente_cuandoPost_entonces404() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId",  "99999999-0000-0000-0000-000000000099",
                "frenteId",    FRENTE_ID,
                "actividadId", ACTIVIDAD_ID,
                "inspectorId", INSPECTOR_ID
        );

        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }
}
