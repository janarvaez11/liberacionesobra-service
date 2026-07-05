package org.ups.liberacionesobra.domain.notification;

import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;

/**
 * Puerto de dominio: notifica a los suscriptores del frente correspondiente
 * cuando se crea una nueva solicitud de inspección. No tiene dependencias de framework.
 */
public interface SolicitudNotificador {

    void notificar(SolicitudInspeccion solicitud);
}
