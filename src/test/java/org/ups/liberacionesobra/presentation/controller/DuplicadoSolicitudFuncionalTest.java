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
@DisplayName("HU-3 · Detección de solicitud duplicada — pruebas funcionales")
class DuplicadoSolicitudFuncionalTest {

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

    private Map<String, Object> bodyBase() {
        return Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID);
    }

    @Test
    @DisplayName("Dado solicitud duplicada sin forzar, Cuando POST, Entonces 409 con SOLICITUD_DUPLICADA")
    void dadoDuplicadoSinForzar_cuandoPost_entonces409() throws Exception {
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyBase())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyBase())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigoError").value("SOLICITUD_DUPLICADA"))
                .andExpect(jsonPath("$.solicitudExistenteId").isNotEmpty());
    }

    @Test
    @DisplayName("Dado solicitud duplicada con forzar=true, Cuando POST, Entonces 201")
    void dadoDuplicadoConForzar_cuandoPost_entonces201() throws Exception {
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyBase())))
                .andExpect(status().isCreated());

        Map<String, Object> bodyForzar = Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID,
                "forzar", true);
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyForzar)))
                .andExpect(status().isCreated());
    }
}
