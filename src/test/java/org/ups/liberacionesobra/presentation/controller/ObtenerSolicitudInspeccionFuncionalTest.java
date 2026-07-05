package org.ups.liberacionesobra.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("HU-1 · Obtener solicitud de inspección por ID — pruebas funcionales")
class ObtenerSolicitudInspeccionFuncionalTest {

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
    @DisplayName("Dado solicitud existente, Cuando GET /solicitudes-inspeccion/{id}, Entonces 200 con datos correctos")
    void dadoSolicitudExistente_cuandoGet_entonces200ConDatos() throws Exception {
        Map<String, Object> body = Map.of(
                "proyectoId", PROYECTO_ID, "frenteId", FRENTE_ID,
                "actividadId", ACTIVIDAD_ID, "inspectorId", INSPECTOR_ID);
        MvcResult result = mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated()).andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/solicitudes-inspeccion/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Dado ID inexistente, Cuando GET /solicitudes-inspeccion/{id}, Entonces 404")
    void dadoIdInexistente_cuandoGet_entonces404() throws Exception {
        mockMvc.perform(get("/solicitudes-inspeccion/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
