package org.ups.liberacionesobra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "frentes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrenteJpaEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "proyecto_id", nullable = false)
    private UUID proyectoId;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "residente_id", nullable = false)
    private UUID residenteId;
}
