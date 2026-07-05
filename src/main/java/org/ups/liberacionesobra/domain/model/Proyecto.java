package org.ups.liberacionesobra.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Proyecto {

    private final UUID   id;
    private final String nombre;
    private final String descripcion;

    private Proyecto(Builder builder) {
        this.id          = builder.id;
        this.nombre      = builder.nombre;
        this.descripcion = builder.descripcion;
    }

    public UUID   getId()          { return id; }
    public String getNombre()      { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proyecto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID   id;
        private String nombre;
        private String descripcion;

        public Builder id(UUID id)             { this.id = id; return this; }
        public Builder nombre(String v)        { this.nombre = v; return this; }
        public Builder descripcion(String v)   { this.descripcion = v; return this; }
        public Proyecto build()                { return new Proyecto(this); }
    }
}
