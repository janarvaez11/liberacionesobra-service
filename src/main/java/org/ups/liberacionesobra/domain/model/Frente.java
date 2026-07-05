package org.ups.liberacionesobra.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Frente {

    private final UUID   id;
    private final UUID   proyectoId;
    private final String nombre;
    private final UUID   residenteId;

    private Frente(Builder builder) {
        this.id          = builder.id;
        this.proyectoId  = builder.proyectoId;
        this.nombre      = builder.nombre;
        this.residenteId = builder.residenteId;
    }

    public UUID   getId()          { return id; }
    public UUID   getProyectoId()  { return proyectoId; }
    public String getNombre()      { return nombre; }
    public UUID   getResidenteId() { return residenteId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Frente that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID   id;
        private UUID   proyectoId;
        private String nombre;
        private UUID   residenteId;

        public Builder id(UUID id)             { this.id = id; return this; }
        public Builder proyectoId(UUID v)      { this.proyectoId = v; return this; }
        public Builder nombre(String v)        { this.nombre = v; return this; }
        public Builder residenteId(UUID v)     { this.residenteId = v; return this; }
        public Frente build()                  { return new Frente(this); }
    }
}
