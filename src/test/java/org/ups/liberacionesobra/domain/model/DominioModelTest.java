package org.ups.liberacionesobra.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain model — equals, hashCode y getters")
class DominioModelTest {

    // ── SolicitudInspeccion ───────────────────────────────────────────────────

    @Test
    @DisplayName("SolicitudInspeccion: misma instancia → equals true")
    void solicitud_mismaInstancia_equalsTrue() {
        SolicitudInspeccion s = solicitudSample();
        assertThat(s).isEqualTo(s);
    }

    @Test
    @DisplayName("SolicitudInspeccion: mismo id → equals true")
    void solicitud_mismoId_equalsTrue() {
        UUID id = UUID.randomUUID();
        SolicitudInspeccion a = solicitudSample(id);
        SolicitudInspeccion b = solicitudSample(id);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("SolicitudInspeccion: distinto id → equals false")
    void solicitud_distintoId_equalsFalse() {
        assertThat(solicitudSample()).isNotEqualTo(solicitudSample());
    }

    @Test
    @DisplayName("SolicitudInspeccion: comparado con null o tipo distinto → false")
    void solicitud_vsNull_equalsFalse() {
        assertThat(solicitudSample().equals(null)).isFalse();
        assertThat(solicitudSample().equals("string")).isFalse();
    }

    // ── Proyecto ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Proyecto: misma instancia → equals true")
    void proyecto_mismaInstancia_equalsTrue() {
        Proyecto p = proyectoSample();
        assertThat(p).isEqualTo(p);
    }

    @Test
    @DisplayName("Proyecto: mismo id → equals true y hashCode igual")
    void proyecto_mismoId_equalsTrue() {
        UUID id = UUID.randomUUID();
        Proyecto a = proyectoSample(id);
        Proyecto b = proyectoSample(id);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("Proyecto: distinto id → equals false")
    void proyecto_distintoId_equalsFalse() {
        assertThat(proyectoSample()).isNotEqualTo(proyectoSample());
    }

    @Test
    @DisplayName("Proyecto: comparado con null → false")
    void proyecto_vsNull_equalsFalse() {
        assertThat(proyectoSample().equals(null)).isFalse();
        assertThat(proyectoSample().equals("otro")).isFalse();
    }

    // ── Frente ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Frente: misma instancia → equals true")
    void frente_mismaInstancia_equalsTrue() {
        Frente f = frenteSample();
        assertThat(f).isEqualTo(f);
    }

    @Test
    @DisplayName("Frente: mismo id → equals true y hashCode igual")
    void frente_mismoId_equalsTrue() {
        UUID id = UUID.randomUUID();
        assertThat(frenteSample(id)).isEqualTo(frenteSample(id));
    }

    @Test
    @DisplayName("Frente: distinto id → equals false")
    void frente_distintoId_equalsFalse() {
        assertThat(frenteSample()).isNotEqualTo(frenteSample());
    }

    @Test
    @DisplayName("Frente: comparado con null → false")
    void frente_vsNull_equalsFalse() {
        assertThat(frenteSample().equals(null)).isFalse();
        assertThat(frenteSample().equals(42)).isFalse();
    }

    // ── Actividad ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Actividad: misma instancia → equals true")
    void actividad_mismaInstancia_equalsTrue() {
        Actividad a = actividadSample();
        assertThat(a).isEqualTo(a);
    }

    @Test
    @DisplayName("Actividad: mismo id → equals true y hashCode igual")
    void actividad_mismoId_equalsTrue() {
        UUID id = UUID.randomUUID();
        assertThat(actividadSample(id)).isEqualTo(actividadSample(id));
    }

    @Test
    @DisplayName("Actividad: distinto id → equals false")
    void actividad_distintoId_equalsFalse() {
        assertThat(actividadSample()).isNotEqualTo(actividadSample());
    }

    @Test
    @DisplayName("Actividad: comparado con null → false")
    void actividad_vsNull_equalsFalse() {
        assertThat(actividadSample().equals(null)).isFalse();
        assertThat(actividadSample().equals(3.14)).isFalse();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SolicitudInspeccion solicitudSample() { return solicitudSample(UUID.randomUUID()); }

    private SolicitudInspeccion solicitudSample(UUID id) {
        return SolicitudInspeccion.builder().id(id)
                .proyectoId(UUID.randomUUID()).frenteId(UUID.randomUUID())
                .actividadId(UUID.randomUUID()).inspectorId(UUID.randomUUID())
                .estado(EstadoSolicitud.PENDIENTE).fechaCreacion(LocalDateTime.now()).build();
    }

    private Proyecto proyectoSample() { return proyectoSample(UUID.randomUUID()); }

    private Proyecto proyectoSample(UUID id) {
        return Proyecto.builder().id(id).nombre("P").descripcion("D").build();
    }

    private Frente frenteSample() { return frenteSample(UUID.randomUUID()); }

    private Frente frenteSample(UUID id) {
        return Frente.builder().id(id).proyectoId(UUID.randomUUID())
                .nombre("F").residenteId(UUID.randomUUID()).build();
    }

    private Actividad actividadSample() { return actividadSample(UUID.randomUUID()); }

    private Actividad actividadSample(UUID id) {
        return Actividad.builder().id(id).frenteId(UUID.randomUUID())
                .nombre("A").descripcion("D").build();
    }
}
