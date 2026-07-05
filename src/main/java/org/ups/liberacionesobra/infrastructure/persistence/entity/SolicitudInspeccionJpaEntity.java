package org.ups.liberacionesobra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_inspeccion")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudInspeccionJpaEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "proyecto_id", nullable = false)
    private UUID proyectoId;

    @Column(name = "frente_id", nullable = false)
    private UUID frenteId;

    @Column(name = "actividad_id", nullable = false)
    private UUID actividadId;

    @Column(name = "inspector_id", nullable = false)
    private UUID inspectorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
