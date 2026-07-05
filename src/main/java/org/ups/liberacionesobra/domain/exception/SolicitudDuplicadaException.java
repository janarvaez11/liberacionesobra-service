package org.ups.liberacionesobra.domain.exception;

import java.util.UUID;

public class SolicitudDuplicadaException extends RuntimeException {

    private final UUID solicitudExistenteId;

    public SolicitudDuplicadaException(UUID solicitudExistenteId) {
        super("Ya existe una solicitud activa para este punto de inspección: " + solicitudExistenteId);
        this.solicitudExistenteId = solicitudExistenteId;
    }

    public UUID getSolicitudExistenteId() { return solicitudExistenteId; }
}
