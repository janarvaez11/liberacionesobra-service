# Plan de Implementación: Crear solicitud de inspección vinculada a frente y actividad

**Rama**: `001-crear-solicitud-inspeccion` | **Fecha**: 2026-06-27 | **Spec**: [spec.md](spec.md)

**Entrada**: Especificación de funcionalidad en `specs/001-crear-solicitud-inspeccion/spec.md`

## Resumen

Implementar el flujo de registro de solicitudes de inspección de obra (US-01, épica E-01).
El inspector de calidad crea una solicitud vinculada a proyecto, frente y actividad; el sistema
la persiste con estado 'pendiente' y la expone vía API REST para que el residente consulte
el listado de su frente. La actualización en la vista del residente se logra mediante polling
automático del cliente. El backend es un servicio REST Java/Spring Boot estructurado con
Arquitectura Limpia y contrato OpenAPI generado por openapi-generator.

## Contexto Técnico

**Lenguaje/Versión**: Java 25

**Dependencias Principales**: Spring Boot 4.1.x · Spring Data JPA · Spring Web MVC ·
Lombok · openapi-generator-gradle-plugin · JaCoCo

**Almacenamiento**: H2 (en memoria para desarrollo e integración; se puede migrar a
PostgreSQL en producción sin cambios en la capa de dominio)

**Pruebas**: JUnit 5 · Mockito · Spring Boot Test (`@SpringBootTest`, `@DataJpaTest`,
`MockMvc`)

**Plataforma Objetivo**: Servidor Linux (JVM) · Clientes móviles y web consumen la API REST

**Tipo de Proyecto**: Servicio web REST (microservicio)

**Objetivos de Rendimiento**: Respuesta < 500 ms en el percentil 95 para los endpoints
principales; polling cada 5 segundos desde el cliente para simular tiempo real

**Restricciones**: Soporte offline fuera del alcance de v1; catálogos de proyecto/frente/actividad
precargados; autenticación fuera del alcance de esta historia

**Alcance/Escala**: Decenas de frentes por proyecto; decenas de solicitudes por frente;
volumen bajo (obra individual)

## Verificación de Constitución

*PUERTA: debe pasar antes de la Fase 0. Se reverifica tras el diseño de la Fase 1.*

| Principio | Regla | Estado |
|-----------|-------|--------|
| I · Arquitectura Limpia | Capas domain / application / infrastructure / presentation; dependencias solo hacia adentro; sin framework en dominio | ✅ Estructura de paquetes diseñada en consecuencia; `CatalogoController` delega en `ConsultarCatalogosUseCase` (no en repositorios directamente) |
| II · BDD Testing | Pruebas unitarias, integración y funcionales con Given-When-Then; pruebas escritas y fallando antes de implementar | ✅ Secciones de prueba en cada fase de tareas |
| III · SOLID/YAGNI/DRY | Sin abstracciones especulativas; polling sobre SSE/WebSockets; DI por interfaces | ✅ Polling elegido (YAGNI); repositorios por interfaz (D) |
| IV · API First | Contrato OpenAPI 3.x aprobado antes de escribir código; clases generadas por openapi-generator | ✅ Contrato en `contracts/openapi.yaml` antes de la implementación |
| V · Calidad (JaCoCo) | Cobertura > 80% por clase; cobertura global >= 80%; build falla si no se cumple | ✅ Configuración de JaCoCo incluida como tarea T002 |

**Reverificación post-diseño (Fase 1)**: ✅ El modelo de datos y el contrato OpenAPI no
introducen violaciones. Sin dependencias de framework en las entidades de dominio.
Sin complejidades no justificadas.

## Estructura del Proyecto

### Documentación (esta funcionalidad)

```text
specs/001-crear-solicitud-inspeccion/
├── plan.md              # Este archivo (/speckit-plan)
├── research.md          # Salida Fase 0 (/speckit-plan)
├── data-model.md        # Salida Fase 1 (/speckit-plan)
├── quickstart.md        # Salida Fase 1 (/speckit-plan)
├── contracts/
│   └── openapi.yaml     # Contrato API OpenAPI 3.x (/speckit-plan)
├── checklists/
│   └── requirements.md  # Lista de verificación (/speckit-specify)
└── tasks.md             # Salida Fase 2 (/speckit-tasks — NO creado por /speckit-plan)
```

### Código Fuente (raíz del repositorio)

```text
src/
├── main/
│   ├── java/org/ups/liberacionesobra/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── SolicitudInspeccion.java  (id, proyectoId, frenteId, actividadId,
│   │   │   │   │                              inspectorId, estado, fechaCreacion)
│   │   │   │   ├── EstadoSolicitud.java       (enum: PENDIENTE, APROBADA, RECHAZADA)
│   │   │   │   ├── Proyecto.java              (id, nombre, descripcion)
│   │   │   │   ├── Frente.java                (id, proyectoId, nombre, residenteId)
│   │   │   │   └── Actividad.java             (id, frenteId, nombre, descripcion)
│   │   │   ├── repository/                    (interfaces — puertos de salida)
│   │   │   │   ├── SolicitudInspeccionRepository.java
│   │   │   │   ├── ProyectoRepository.java
│   │   │   │   ├── FrenteRepository.java
│   │   │   │   └── ActividadRepository.java
│   │   │   ├── service/
│   │   │   │   └── SolicitudDomainService.java
│   │   │   └── exception/
│   │   │       ├── SolicitudNotFoundException.java
│   │   │       └── SolicitudDuplicadaException.java
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       ├── CrearSolicitudInspeccionUseCase.java
│   │   │       ├── ObtenerSolicitudInspeccionUseCase.java  (GET /{id})
│   │   │       ├── ConsultarSolicitudesFrenteUseCase.java
│   │   │       └── ConsultarCatalogosUseCase.java          (proyectos, frentes, actividades)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── SolicitudInspeccionJpaEntity.java
│   │   │   │   │   ├── ProyectoJpaEntity.java
│   │   │   │   │   ├── FrenteJpaEntity.java
│   │   │   │   │   └── ActividadJpaEntity.java
│   │   │   │   ├── mapper/
│   │   │   │   │   ├── SolicitudMapper.java    (dominio ↔ JPA entity)
│   │   │   │   │   ├── ProyectoMapper.java     (dominio ↔ JPA entity)
│   │   │   │   │   ├── FrenteMapper.java       (dominio ↔ JPA entity)
│   │   │   │   │   └── ActividadMapper.java    (dominio ↔ JPA entity)
│   │   │   │   ├── jpa/
│   │   │   │   │   ├── SolicitudInspeccionJpaRepository.java
│   │   │   │   │   ├── ProyectoJpaRepository.java
│   │   │   │   │   ├── FrenteJpaRepository.java
│   │   │   │   │   └── ActividadJpaRepository.java
│   │   │   │   ├── SolicitudInspeccionRepositoryImpl.java
│   │   │   │   ├── ProyectoRepositoryImpl.java
│   │   │   │   ├── FrenteRepositoryImpl.java
│   │   │   │   └── ActividadRepositoryImpl.java
│   │   │   └── config/
│   │   └── presentation/
│   │       ├── controller/
│   │       │   ├── SolicitudInspeccionController.java  (SolicitudesApi generada)
│   │       │   └── CatalogoController.java              (CatalogoApi generada)
│   │       ├── mapper/
│   │       │   ├── SolicitudDtoMapper.java    (DTO ↔ dominio)
│   │       │   └── CatalogoDtoMapper.java     (DTOs catálogo ↔ dominio)
│   │       └── exception/
│   │           └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yaml
│       ├── data.sql
│       └── openapi/
│           └── liberacionesobra-api.yaml      (contrato OpenAPI — fuente de verdad)
└── test/
    └── java/org/ups/liberacionesobra/
        ├── domain/                            (pruebas unitarias — dominio)
        ├── application/                       (pruebas unitarias — casos de uso)
        ├── infrastructure/                    (pruebas de integración — @DataJpaTest)
        └── presentation/                      (pruebas funcionales — MockMvc)
```

**Decisión de Estructura**: Proyecto único con Arquitectura Limpia. Paquetes separados por
capa según el Principio I de la constitución. Las interfaces de controlador y DTOs se generan
desde el contrato OpenAPI ubicado en `src/main/resources/openapi/`.

## Seguimiento de Complejidad

> *Se completa solo si la Verificación de Constitución tiene violaciones que deben justificarse*

Sin violaciones detectadas. No aplica.
