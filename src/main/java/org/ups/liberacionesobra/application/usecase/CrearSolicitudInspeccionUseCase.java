package org.ups.liberacionesobra.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ups.liberacionesobra.domain.exception.SolicitudDuplicadaException;
import org.ups.liberacionesobra.domain.model.EstadoSolicitud;
import org.ups.liberacionesobra.domain.model.SolicitudInspeccion;
import org.ups.liberacionesobra.domain.notification.SolicitudNotificador;
import org.ups.liberacionesobra.domain.repository.ActividadRepository;
import org.ups.liberacionesobra.domain.repository.FrenteRepository;
import org.ups.liberacionesobra.domain.repository.ProyectoRepository;
import org.ups.liberacionesobra.domain.repository.SolicitudInspeccionRepository;
import org.ups.liberacionesobra.domain.service.SolicitudDomainService;

import java.util.UUID;

/**
 * Caso de uso: registrar una nueva solicitud de inspección vinculada a proyecto,
 * frente y actividad. Detecta duplicados activos y delega la construcción
 * de la entidad al servicio de dominio.
 */
@Service
public class CrearSolicitudInspeccionUseCase {

    private static final Logger log = LoggerFactory.getLogger(CrearSolicitudInspeccionUseCase.class);

    private final SolicitudInspeccionRepository solicitudRepo;
    private final ProyectoRepository            proyectoRepo;
    private final FrenteRepository              frenteRepo;
    private final ActividadRepository           actividadRepo;
    private final SolicitudDomainService        domainService;
    private final SolicitudNotificador          notificador;

    public CrearSolicitudInspeccionUseCase(SolicitudInspeccionRepository solicitudRepo,
                                           ProyectoRepository proyectoRepo,
                                           FrenteRepository frenteRepo,
                                           ActividadRepository actividadRepo,
                                           SolicitudDomainService domainService,
                                           SolicitudNotificador notificador) {
        this.solicitudRepo = solicitudRepo;
        this.proyectoRepo  = proyectoRepo;
        this.frenteRepo    = frenteRepo;
        this.actividadRepo = actividadRepo;
        this.domainService = domainService;
        this.notificador   = notificador;
    }

    /**
     * Crea y persiste una solicitud de inspección.
     *
     * @param proyectoId  UUID del proyecto al que pertenece el punto de inspección
     * @param frenteId    UUID del frente de obra
     * @param actividadId UUID de la actividad a inspeccionar
     * @param inspectorId UUID del inspector que abre la solicitud
     * @param forzar      si {@code true} omite la verificación de duplicados
     * @return la solicitud persistida con su identificador asignado
     * @throws IllegalArgumentException   si proyecto, frente o actividad no existen en el catálogo
     * @throws org.ups.liberacionesobra.domain.exception.SolicitudDuplicadaException
     *                                    si ya existe una solicitud PENDIENTE y {@code forzar} es {@code false}
     */
    public SolicitudInspeccion ejecutar(UUID proyectoId, UUID frenteId,
                                        UUID actividadId, UUID inspectorId, boolean forzar) {
        // 1. Validar existencia en catálogos
        proyectoRepo.buscarPorId(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + proyectoId));
        frenteRepo.buscarPorId(frenteId)
                .orElseThrow(() -> new IllegalArgumentException("Frente no encontrado: " + frenteId));
        actividadRepo.buscarPorId(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada: " + actividadId));

        // 2. Detectar duplicados (omitir si forzar=true)
        if (!forzar && solicitudRepo.existePendientePara(proyectoId, frenteId, actividadId)) {
            UUID existenteId = solicitudRepo
                    .buscarPorFiltros(frenteId, proyectoId, EstadoSolicitud.PENDIENTE)
                    .stream().findFirst()
                    .map(SolicitudInspeccion::getId)
                    .orElse(UUID.randomUUID());
            log.warn("Solicitud duplicada detectada para proyecto={} frente={} actividad={}",
                    proyectoId, frenteId, actividadId);
            throw new SolicitudDuplicadaException(existenteId);
        }

        // 3. Construir entidad de dominio
        SolicitudInspeccion solicitud = domainService.crearSolicitud(proyectoId, frenteId, actividadId, inspectorId);

        // 4. Persistir
        SolicitudInspeccion persistida = solicitudRepo.guardar(solicitud);
        log.info("Solicitud de inspección creada: id={} proyecto={} frente={} actividad={}",
                persistida.getId(), proyectoId, frenteId, actividadId);

        // 5. Notificar en tiempo real a los residentes suscritos al frente (RF-005)
        notificador.notificar(persistida);
        return persistida;
    }
}
