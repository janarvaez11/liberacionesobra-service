package org.ups.liberacionesobra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "actividades")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadJpaEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "frente_id", nullable = false)
    private UUID frenteId;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
