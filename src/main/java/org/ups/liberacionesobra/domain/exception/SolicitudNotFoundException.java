package org.ups.liberacionesobra.domain.exception;

import java.util.UUID;

public class SolicitudNotFoundException extends RuntimeException {

    private final UUID solicitudId;

    public SolicitudNotFoundException(UUID solicitudId) {
        super("Solicitud de inspección no encontrada: " + solicitudId);
        this.solicitudId = solicitudId;
    }

    public UUID getSolicitudId() { return solicitudId; }
}
