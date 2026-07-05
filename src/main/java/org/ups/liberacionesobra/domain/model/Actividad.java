package org.ups.liberacionesobra.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Actividad {

    private final UUID   id;
    private final UUID   frenteId;
    private final String nombre;
    private final String descripcion;

    private Actividad(Builder builder) {
        this.id          = builder.id;
        this.frenteId    = builder.frenteId;
        this.nombre      = builder.nombre;
        this.descripcion = builder.descripcion;
    }

    public UUID   getId()          { return id; }
    public UUID   getFrenteId()    { return frenteId; }
    public String getNombre()      { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actividad that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID   id;
        private UUID   frenteId;
        private String nombre;
        private String descripcion;

        public Builder id(UUID id)            { this.id = id; return this; }
        public Builder frenteId(UUID v)       { this.frenteId = v; return this; }
        public Builder nombre(String v)       { this.nombre = v; return this; }
        public Builder descripcion(String v)  { this.descripcion = v; return this; }
        public Actividad build()              { return new Actividad(this); }
    }
}
