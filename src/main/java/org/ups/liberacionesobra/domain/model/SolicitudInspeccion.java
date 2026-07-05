package org.ups.liberacionesobra.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SolicitudInspeccion {

    private final UUID id;
    private final UUID proyectoId;
    private final UUID frenteId;
    private final UUID actividadId;
    private final UUID inspectorId;
    private final EstadoSolicitud estado;
    private final LocalDateTime fechaCreacion;

    private SolicitudInspeccion(Builder builder) {
        this.id           = builder.id;
        this.proyectoId   = builder.proyectoId;
        this.frenteId     = builder.frenteId;
        this.actividadId  = builder.actividadId;
        this.inspectorId  = builder.inspectorId;
        this.estado       = builder.estado;
        this.fechaCreacion = builder.fechaCreacion;
    }

    public UUID getId()                    { return id; }
    public UUID getProyectoId()            { return proyectoId; }
    public UUID getFrenteId()              { return frenteId; }
    public UUID getActividadId()           { return actividadId; }
    public UUID getInspectorId()           { return inspectorId; }
    public EstadoSolicitud getEstado()     { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitudInspeccion that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private UUID proyectoId;
        private UUID frenteId;
        private UUID actividadId;
        private UUID inspectorId;
        private EstadoSolicitud estado;
        private LocalDateTime fechaCreacion;

        public Builder id(UUID id)                          { this.id = id; return this; }
        public Builder proyectoId(UUID v)                   { this.proyectoId = v; return this; }
        public Builder frenteId(UUID v)                     { this.frenteId = v; return this; }
        public Builder actividadId(UUID v)                  { this.actividadId = v; return this; }
        public Builder inspectorId(UUID v)                  { this.inspectorId = v; return this; }
        public Builder estado(EstadoSolicitud v)            { this.estado = v; return this; }
        public Builder fechaCreacion(LocalDateTime v)       { this.fechaCreacion = v; return this; }
        public SolicitudInspeccion build()                  { return new SolicitudInspeccion(this); }
    }
}
