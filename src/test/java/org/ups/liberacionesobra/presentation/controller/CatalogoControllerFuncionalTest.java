package org.ups.liberacionesobra.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Catálogos — pruebas funcionales (GET proyectos, frentes, actividades)")
class CatalogoControllerFuncionalTest {

    @Autowired WebApplicationContext context;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String PROYECTO_ID = "11111111-0000-0000-0000-000000000001";
    private static final String FRENTE_ID   = "22222222-0000-0000-0000-000000000001";

    @Test
    @DisplayName("Cuando GET /proyectos, Entonces 200 con el proyecto semilla")
    void cuandoGetProyectos_entonces200ConProyectoSemilla() throws Exception {
        mockMvc.perform(get("/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proyectos").isArray())
                .andExpect(jsonPath("$.proyectos[0].nombre").value("Proyecto Alfa"));
    }

    @Test
    @DisplayName("Cuando GET /proyectos/{id}/frentes, Entonces 200 con el frente semilla")
    void cuandoGetFrentes_entonces200ConFrenteSemilla() throws Exception {
        mockMvc.perform(get("/proyectos/" + PROYECTO_ID + "/frentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frentes").isArray())
                .andExpect(jsonPath("$.frentes[0].nombre").value("Frente Norte"));
    }

    @Test
    @DisplayName("Cuando GET /frentes/{id}/actividades, Entonces 200 con la actividad semilla")
    void cuandoGetActividades_entonces200ConActividadSemilla() throws Exception {
        mockMvc.perform(get("/frentes/" + FRENTE_ID + "/actividades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actividades").isArray())
                .andExpect(jsonPath("$.actividades[0].nombre").value("Encofrado columnas"));
    }

    @Test
    @DisplayName("Dado proyecto inexistente, Cuando GET /proyectos/{id}/frentes, Entonces 404")
    void dadoProyectoInexistente_cuandoGetFrentes_entonces404() throws Exception {
        mockMvc.perform(get("/proyectos/99999999-0000-0000-0000-000000000099/frentes"))
                .andExpect(status().isNotFound());
    }
}
