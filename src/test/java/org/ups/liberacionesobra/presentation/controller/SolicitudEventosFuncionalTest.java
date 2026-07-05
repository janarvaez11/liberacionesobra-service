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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RF-005: prueba funcional de extremo a extremo del mecanismo de actualización
 * automática. Demuestra que existe un endpoint de suscripción SSE y que, al crear
 * una solicitud, el sistema empuja la actualización al residente suscrito sin que
 * este haga ninguna acción manual (ni recargar, ni volver a pedir el listado).
 */
@SpringBootTest
@Transactional
@DisplayName("RF-005 · Suscripción SSE del frente — pruebas funcionales")
class SolicitudEventosFuncionalTest {

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
    @DisplayName("Dado residente suscrito al frente, Cuando un inspector crea una solicitud, "
            + "Entonces el residente recibe la actualización automáticamente sin acción manual")
    void dadoResidenteSuscrito_cuandoInspectorCreaSolicitud_entoncesRecibeActualizacionAutomatica() throws Exception {
        MvcResult suscripcion = mockMvc.perform(get("/frentes/{frenteId}/solicitudes-inspeccion/eventos", FRENTE_ID))
                .andExpect(request().asyncStarted())
                .andReturn();

        Map<String, Object> body = Map.of(
                "proyectoId",  PROYECTO_ID,
                "frenteId",    FRENTE_ID,
                "actividadId", ACTIVIDAD_ID,
                "inspectorId", INSPECTOR_ID
        );
        mockMvc.perform(post("/solicitudes-inspeccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // La suscripción SSE permanece abierta por diseño (RF-005: push continuo, no una
        // respuesta que "termina"), así que se inspecciona lo ya escrito en el stream en
        // vez de esperar a que la conexión se cierre.
        String contenidoStream = suscripcion.getResponse().getContentAsString();
        assertThat(contenidoStream).contains("nueva-solicitud");
        assertThat(contenidoStream).contains(FRENTE_ID);
    }

    @Test
    @DisplayName("Dado ninguna solicitud creada, Cuando un residente se suscribe, Entonces la conexión SSE se abre correctamente")
    void dadoSinSolicitudes_cuandoSeSuscribe_entoncesConexionSeAbre() throws Exception {
        mockMvc.perform(get("/frentes/{frenteId}/solicitudes-inspeccion/eventos", FRENTE_ID))
                .andExpect(request().asyncStarted());
    }
}
