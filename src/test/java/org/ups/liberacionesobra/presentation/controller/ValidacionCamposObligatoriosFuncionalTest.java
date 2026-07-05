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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("HU-3 · Validación de campos obligatorios — pruebas funcionales")
class ValidacionCamposObligatoriosFuncionalTest {

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
    @DisplayName("Dado cuerpo vacío, Cuando POST, Entonces 400 con errores por campo")
    void dadoCuerpoVacio_cuandoPost_entonces400ConErrores() throws Exception {
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    @DisplayName("Dado frenteId faltante, Cuando POST, Entonces 400 con error específico de frenteId")
    void dadoFrenteIdFaltante_cuandoPost_entonces400ConErrorFrenteId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("proyectoId",  PROYECTO_ID);
        body.put("actividadId", ACTIVIDAD_ID);
        body.put("inspectorId", INSPECTOR_ID);

        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[?(@.campo == 'frenteId')]").exists());
    }

    @Test
    @DisplayName("Dado actividadId faltante, Cuando POST, Entonces 400 con error específico de actividadId")
    void dadoActividadIdFaltante_cuandoPost_entonces400ConErrorActividadId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("proyectoId",  PROYECTO_ID);
        body.put("frenteId",    FRENTE_ID);
        body.put("inspectorId", INSPECTOR_ID);

        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[?(@.campo == 'actividadId')]").exists());
    }
}
