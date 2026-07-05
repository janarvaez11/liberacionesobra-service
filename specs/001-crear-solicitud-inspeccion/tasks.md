---
description: "Lista de tareas para la implementaciÃ³n de la solicitud de inspecciÃ³n"
---

# Tareas: Crear solicitud de inspecciÃ³n vinculada a frente y actividad

**Entrada**: Documentos de diseÃ±o en `specs/001-crear-solicitud-inspeccion/`

**Prerequisitos**: plan.md âœ… Â· spec.md âœ… Â· research.md âœ… Â· data-model.md âœ… Â· contracts/ âœ…

**RevisiÃ³n v2** (2026-06-27): Incorpora correcciones C1, F1, F2, F3, F4 del anÃ¡lisis:
- C1: `CatalogoController` delega en `ConsultarCatalogosUseCase` (no en repositorios directamente)
- F1: Tareas para `ObtenerSolicitudInspeccionUseCase` y mÃ©todo `obtenerSolicitudInspeccion` del controlador
- F2: Tareas para `ProyectoRepositoryImpl`, `FrenteRepositoryImpl`, `ActividadRepositoryImpl`
- F3: Tareas para `ProyectoMapper`, `FrenteMapper`, `ActividadMapper` en infraestructura
- F4: T007 expandido con atributos explÃ­citos por entidad y guÃ­a de Lombok

**Pruebas**: Las pruebas BDD son **obligatorias** segÃºn el Principio II de la constituciÃ³n.
Deben escribirse ANTES de la implementaciÃ³n y confirmarse fallidas antes de implementar.

**OrganizaciÃ³n**: Las tareas se agrupan por historia de usuario para permitir implementaciÃ³n y
prueba independiente de cada historia.

## Formato: `[ID] [P?] [Historia?] DescripciÃ³n`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias)
- **[Historia]**: A quÃ© historia de usuario pertenece esta tarea (US1, US2, US3)
- Incluir ruta exacta de archivo en cada descripciÃ³n

## Convenciones de Ruta

- RaÃ­z del paquete Java: `src/main/java/org/ups/liberacionesobra/`
- RaÃ­z de pruebas Java: `src/test/java/org/ups/liberacionesobra/`
- Recursos: `src/main/resources/`

---

## Fase 1: ConfiguraciÃ³n del Proyecto

**PropÃ³sito**: InicializaciÃ³n de infraestructura de build y estructura de paquetes.

- [X] T001 Configurar `openapi-generator-gradle-plugin` en `build.gradle`: aÃ±adir plugin, definir tarea `generateOpenApiClasses` apuntando a `src/main/resources/openapi/liberacionesobra-api.yaml`, generador `spring`, paquetes base `org.ups.liberacionesobra.presentation.generated`, directorio de salida `build/generated-sources/openapi`, aÃ±adir directorio al `compileJava` source set, excluir directorio del `.gitignore`
- [X] T002 Configurar plugin `jacoco` en `build.gradle`: aÃ±adir `jacocoTestReport` (HTML + XML en `build/reports/jacoco/`), aÃ±adir `jacocoTestCoverageVerification` con umbral de instrucciones >= 0.80 y ramas >= 0.80, hacer que la tarea `check` dependa de `jacocoTestCoverageVerification`, excluir patrÃ³n `org/ups/liberacionesobra/presentation/generated/**`
- [X] T003 [P] Copiar `specs/001-crear-solicitud-inspeccion/contracts/openapi.yaml` a `src/main/resources/openapi/liberacionesobra-api.yaml` (fuente de verdad del contrato API)
- [X] T004 [P] Crear estructura de paquetes Clean Architecture bajo `src/main/java/org/ups/liberacionesobra/`: `domain/model/`, `domain/repository/`, `domain/service/`, `domain/exception/`, `application/usecase/`, `infrastructure/persistence/entity/`, `infrastructure/persistence/jpa/`, `infrastructure/persistence/mapper/`, `presentation/controller/`, `presentation/mapper/`, `presentation/exception/`

---

## Fase 2: Fundamentos (Prerequisitos Bloqueantes)

**PropÃ³sito**: Infraestructura central que DEBE completarse antes de cualquier historia de usuario.

**âš ï¸ CRÃTICO**: NingÃºn trabajo de historia de usuario puede comenzar hasta que esta fase estÃ© completa.

- [X] T005 Configurar `src/main/resources/application.yaml` con datasource H2 (`spring.datasource.url=jdbc:h2:mem:liberaciones`, `driver-class-name=org.h2.Driver`, `username=sa`, `password=`), JPA (`ddl-auto=create-drop`, `show-sql=true`), H2 console (`spring.h2.console.enabled=true`)
- [X] T006 Crear `src/main/resources/data.sql` con datos de catÃ¡logo iniciales: Proyecto `id=11111111-0000-0000-0000-000000000001` nombre="Proyecto Alfa"; Frente `id=22222222-0000-0000-0000-000000000001` proyectoId=11111... nombre="Frente Norte" residenteId=55555555-...; Actividad `id=33333333-0000-0000-0000-000000000001` frenteId=22222... nombre="Encofrado columnas" â€” coinciden con datos del `quickstart.md`
- [X] T007 [P] Crear entidades de dominio en `src/main/java/org/ups/liberacionesobra/domain/model/` con los atributos exactos indicados â€” `@Getter` y `@Builder` permitidos; `@Data` **prohibido**; `equals`/`hashCode` implementados manualmente basados en `id` (sin anotaciones de Spring ni JPA):
  - `SolicitudInspeccion.java` (id UUID, proyectoId UUID, frenteId UUID, actividadId UUID, inspectorId UUID, estado EstadoSolicitud, fechaCreacion LocalDateTime)
  - `EstadoSolicitud.java` (enum: PENDIENTE, APROBADA, RECHAZADA)
  - `Proyecto.java` (id UUID, nombre String, descripcion String)
  - `Frente.java` (id UUID, proyectoId UUID, nombre String, residenteId UUID)
  - `Actividad.java` (id UUID, frenteId UUID, nombre String, descripcion String)
- [X] T008 [P] Crear interfaces de repositorio (puertos de salida) en `src/main/java/org/ups/liberacionesobra/domain/repository/`: `SolicitudInspeccionRepository.java` (guardar, buscarPorId, existePendientePara(proyectoId,frenteId,actividadId), buscarPorFiltros); `ProyectoRepository.java` (buscarPorId, listarTodos); `FrenteRepository.java` (buscarPorId, listarPorProyecto); `ActividadRepository.java` (buscarPorId, listarPorFrente)
- [X] T009 [P] Crear entidades JPA en `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/entity/` con el siguiente alcance diferenciado: (a) `SolicitudInspeccionJpaEntity.java` como **esqueleto vacÃ­o** â€” clase con `@Entity`, `@Table(name="solicitudes_inspeccion")` y `@Id` Ãºnicamente, sin campos todavÃ­a (los campos completos se aÃ±aden en T027 para no duplicar trabajo); (b) `ProyectoJpaEntity.java` (id, nombre, descripcion), `FrenteJpaEntity.java` (id, proyectoId, nombre, residenteId), `ActividadJpaEntity.java` (id, frenteId, nombre, descripcion) con implementaciÃ³n completa de sus campos `@Column`, ya que no tienen tarea de refinamiento posterior â€” ninguna es la misma clase que su entidad de dominio homÃ³loga
- [X] T010 Crear repositorios Spring Data JPA en `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/jpa/` con el siguiente alcance diferenciado: (a) `SolicitudInspeccionJpaRepository.java` como **esqueleto vacÃ­o** â€” interfaz que extiende `JpaRepository<SolicitudInspeccionJpaEntity, UUID>` sin mÃ©todos personalizados todavÃ­a (los mÃ©todos completos se aÃ±aden en T028); (b) `ProyectoJpaRepository.java`, `FrenteJpaRepository.java` (con `findByProyectoId(UUID proyectoId)`), `ActividadJpaRepository.java` (con `findByFrenteId(UUID frenteId)`) con implementaciÃ³n completa, ya que no tienen tarea de refinamiento posterior
- [X] T011 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/ProyectoRepositoryImpl.java` â€” implementa `domain/repository/ProyectoRepository`, delega en `ProyectoJpaRepository`, usa `ProyectoMapper` para conversiÃ³n; anotada con `@Repository`
- [X] T012 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/FrenteRepositoryImpl.java` â€” implementa `domain/repository/FrenteRepository`, delega en `FrenteJpaRepository`, usa `FrenteMapper`; anotada con `@Repository`
- [X] T013 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/ActividadRepositoryImpl.java` â€” implementa `domain/repository/ActividadRepository`, delega en `ActividadJpaRepository`, usa `ActividadMapper`; anotada con `@Repository`
- [X] T014 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/mapper/ProyectoMapper.java` â€” convierte `Proyecto` (dominio) â†” `ProyectoJpaEntity` (infraestructura)
- [X] T015 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/mapper/FrenteMapper.java` â€” convierte `Frente` (dominio) â†” `FrenteJpaEntity`
- [X] T016 [P] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/mapper/ActividadMapper.java` â€” convierte `Actividad` (dominio) â†” `ActividadJpaEntity`
- [X] T017 [P] Crear `src/main/java/org/ups/liberacionesobra/presentation/exception/GlobalExceptionHandler.java` como `@ControllerAdvice` esqueleto â€” sin lÃ³gica aÃºn, solo la anotaciÃ³n y la clase
- [X] T018 Verificar que `./gradlew compileJava` compila sin errores y que el cÃ³digo generado por OpenAPI aparece en `build/generated-sources/openapi/` â€” corregir cualquier problema de compilaciÃ³n antes de continuar

**Punto de control**: Fundamentos listos â€” la implementaciÃ³n de historias de usuario puede comenzar.

---

## Fase 3: Historia de Usuario 1 â€” Registrar solicitud de inspecciÃ³n (Prioridad: P1) â­ MVP

**Objetivo**: El inspector puede crear una solicitud de inspecciÃ³n vÃ¡lida y el sistema la persiste
con estado 'PENDIENTE' y fecha de creaciÃ³n. El endpoint `GET /solicitudes-inspeccion/{id}`
funciona (requerido por la interfaz generada `SolicitudesApi`).

**Prueba Independiente**: Ejecutar Escenarios 1 del `quickstart.md` â€” POST a
`/solicitudes-inspeccion` devuelve HTTP 201; GET `/{id}` devuelve HTTP 200 con la solicitud.

### Pruebas BDD para Historia de Usuario 1 (escribir PRIMERO â€” deben FALLAR antes de implementar) âš ï¸

> **IMPORTANTE**: Escribir estas pruebas y confirmar que fallan antes de cualquier implementaciÃ³n.

- [X] T019 [P] [US1] Crear prueba unitaria `src/test/java/org/ups/liberacionesobra/application/usecase/CrearSolicitudInspeccionUseCaseTest.java` â€” **Dado** datos de solicitud vÃ¡lidos, **Cuando** se invoca el caso de uso, **Entonces** la solicitud devuelta tiene estado PENDIENTE y fechaCreacion no nula (Mockito para repositorios)
- [X] T020 [P] [US1] Crear prueba de integraciÃ³n `src/test/java/org/ups/liberacionesobra/infrastructure/persistence/SolicitudInspeccionJpaRepositoryTest.java` (@DataJpaTest) â€” **Dado** una entidad JPA de solicitud construida con todos sus campos, **Cuando** se guarda y se recupera por ID, **Entonces** todos los campos persisten correctamente
- [X] T021 [P] [US1] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/CrearSolicitudInspeccionFuncionalTest.java` (@SpringBootTest + MockMvc) â€” **Dado** datos vÃ¡lidos de proyecto/frente/actividad/inspector, **Cuando** se hace POST a `/solicitudes-inspeccion`, **Entonces** la respuesta es HTTP 201 con `id`, `estado: "PENDIENTE"` y `fechaCreacion`
- [X] T022 [P] [US1] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/ObtenerSolicitudInspeccionFuncionalTest.java` (@SpringBootTest + MockMvc) â€” **Dado** una solicitud existente con ID conocido, **Cuando** se hace GET a `/solicitudes-inspeccion/{id}`, **Entonces** la respuesta es HTTP 200 con los datos de la solicitud; **Dado** un ID inexistente, **Cuando** se hace GET, **Entonces** la respuesta es HTTP 404

### ImplementaciÃ³n de Historia de Usuario 1

- [X] T023 [US1] Implementar `src/main/java/org/ups/liberacionesobra/domain/service/SolicitudDomainService.java` â€” mÃ©todo `crearSolicitud(UUID proyectoId, UUID frenteId, UUID actividadId, UUID inspectorId)` que genera un UUID nuevo, asigna estado PENDIENTE y asigna `LocalDateTime.now()` como fechaCreacion; retorna `SolicitudInspeccion`; sin anotaciones de Spring y sin dependencias de repositorios â€” es un servicio de construcciÃ³n de entidad de dominio puro
- [X] T024 [US1] Implementar `src/main/java/org/ups/liberacionesobra/application/usecase/CrearSolicitudInspeccionUseCase.java` â€” orquesta el flujo completo en este orden: (1) valida existencia de proyecto/frente/actividad en sus repositorios de dominio (lanza `IllegalArgumentException` si no existen); (2) comprueba duplicados llamando a `SolicitudInspeccionRepository.existePendientePara(proyectoId, frenteId, actividadId)` â€” si existe y `forzar=false` lanza `SolicitudDuplicadaException`; (3) delega la construcciÃ³n del objeto en `SolicitudDomainService.crearSolicitud`; (4) persiste con `SolicitudInspeccionRepository.guardar`; anotado con `@Service`
- [X] T025 [US1] Implementar `src/main/java/org/ups/liberacionesobra/application/usecase/ObtenerSolicitudInspeccionUseCase.java` â€” busca solicitud por id en `SolicitudInspeccionRepository`; lanza `SolicitudNotFoundException` si no se encuentra; anotado con `@Service`
- [X] T026 [US1] Crear `src/main/java/org/ups/liberacionesobra/domain/exception/SolicitudNotFoundException.java` â€” excepciÃ³n de negocio sin anotaciones de framework; incluye `solicitudId` (UUID) en el mensaje
- [X] T027 [US1] Completar el esqueleto de `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/entity/SolicitudInspeccionJpaEntity.java` creado en T009 aÃ±adiendo todos sus campos: `proyectoId UUID`, `frenteId UUID`, `actividadId UUID`, `inspectorId UUID` con `@Column(nullable=false)`; `estado EstadoSolicitud` con `@Enumerated(EnumType.STRING)` y `nullable=false`; `fechaCreacion LocalDateTime` con `@Column(nullable=false)`; `id UUID` con `@Id @GeneratedValue(strategy=GenerationType.UUID)`
- [X] T028 [US1] Completar el esqueleto de `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/jpa/SolicitudInspeccionJpaRepository.java` creado en T010 aÃ±adiendo los mÃ©todos personalizados: `boolean existsByProyectoIdAndFrenteIdAndActividadIdAndEstado(UUID, UUID, UUID, EstadoSolicitud)` y `List<SolicitudInspeccionJpaEntity> findByFrenteIdAndEstado(UUID frenteId, EstadoSolicitud estado)`
- [X] T029 [US1] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/SolicitudInspeccionRepositoryImpl.java` â€” implementa `domain/repository/SolicitudInspeccionRepository`; delega en `SolicitudInspeccionJpaRepository`; usa `SolicitudMapper` para conversiÃ³n; anotada con `@Repository`
- [X] T030 [US1] Implementar `src/main/java/org/ups/liberacionesobra/infrastructure/persistence/mapper/SolicitudMapper.java` â€” convierte `SolicitudInspeccion` (dominio) â†” `SolicitudInspeccionJpaEntity`
- [X] T031 [US1] Implementar `src/main/java/org/ups/liberacionesobra/presentation/controller/SolicitudInspeccionController.java` â€” implementa la interfaz generada `SolicitudesApi`; implementa AMBOS mÃ©todos: `crearSolicitudInspeccion` (delega en `CrearSolicitudInspeccionUseCase`) y `obtenerSolicitudInspeccion` (delega en `ObtenerSolicitudInspeccionUseCase`); usa `SolicitudDtoMapper`
- [X] T032 [US1] Implementar `src/main/java/org/ups/liberacionesobra/presentation/mapper/SolicitudDtoMapper.java` â€” `CrearSolicitudRequest` (DTO generado) â†’ comando de dominio; `SolicitudInspeccion` (dominio) â†’ `SolicitudInspeccionResponse` (DTO generado)
- [X] T033 [US1] Mapear `SolicitudNotFoundException` â†’ HTTP 404 en `src/main/java/org/ups/liberacionesobra/presentation/exception/GlobalExceptionHandler.java`
- [X] T034 [US1] Validar Escenarios: ejecutar `./gradlew bootRun` y confirmar que (a) POST a `/solicitudes-inspeccion` con datos vÃ¡lidos â†’ HTTP 201 con `id`, `estado: "PENDIENTE"`, `fechaCreacion`; (b) GET `/solicitudes-inspeccion/{id}` â†’ HTTP 200 con la solicitud; (c) GET con id inexistente â†’ HTTP 404

**Punto de control**: La Historia de Usuario 1 es completamente funcional e independientemente verificable (MVP).

---

## Fase 4: Historia de Usuario 2 â€” Consultar solicitudes del frente en tiempo real (Prioridad: P2)

**Objetivo**: El residente puede consultar las solicitudes de su frente; las nuevas solicitudes
aparecen al consultar sin recargar la pantalla (el cliente hace polling cada 5 segundos).

**Prueba Independiente**: Ejecutar Escenario 2 del `quickstart.md` â€” GET a
`/solicitudes-inspeccion?frenteId=...` devuelve HTTP 200 con la solicitud creada en HU-1.

### Pruebas BDD para Historia de Usuario 2 (escribir PRIMERO â€” deben FALLAR antes de implementar) âš ï¸

- [X] T035 [P] [US2] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/ConsultarSolicitudesFrenteFuncionalTest.java` (@SpringBootTest + MockMvc) â€” **Dado** una solicitud PENDIENTE existente para el frente 22222222-..., **Cuando** el residente hace GET a `/solicitudes-inspeccion?frenteId=22222222-...`, **Entonces** la respuesta es HTTP 200 con la solicitud incluida en el campo `solicitudes` del cuerpo

### ImplementaciÃ³n de Historia de Usuario 2

- [X] T036 [US2] Agregar mÃ©todo `buscarPorFiltros(UUID frenteId, UUID proyectoId, EstadoSolicitud estado)` a la interfaz `src/main/java/org/ups/liberacionesobra/domain/repository/SolicitudInspeccionRepository.java` y su implementaciÃ³n en `SolicitudInspeccionRepositoryImpl.java`
- [X] T037 [US2] Implementar `src/main/java/org/ups/liberacionesobra/application/usecase/ConsultarSolicitudesFrenteUseCase.java` â€” acepta frenteId, proyectoId y estado como filtros opcionales; retorna `List<SolicitudInspeccion>`; anotado con `@Service`
- [X] T038 [US2] Agregar mÃ©todo `listarSolicitudesInspeccion` (GET /solicitudes-inspeccion con query params: frenteId, proyectoId, estado) a `src/main/java/org/ups/liberacionesobra/presentation/controller/SolicitudInspeccionController.java`, delegando en `ConsultarSolicitudesFrenteUseCase`
- [X] T039 [US2] Validar Escenario 2 del `quickstart.md`: crear una solicitud y verificar que aparece en el GET al listado del frente; simular polling repitiendo la llamada GET y verificar consistencia de resultados

**Punto de control**: Las Historias de Usuario 1 y 2 funcionan de forma independiente.

---

## Fase 5: Historia de Usuario 3 â€” Validar datos obligatorios y controlar duplicados (Prioridad: P3)

**Objetivo**: El sistema bloquea solicitudes con campos vacÃ­os (HTTP 400 por campo) y advierte
ante duplicados activos (HTTP 409) requiriendo confirmaciÃ³n explÃ­cita (`forzar: true`).

**Prueba Independiente**: Ejecutar Escenarios 3, 4 y 5 del `quickstart.md`.

### Pruebas BDD para Historia de Usuario 3 (escribir PRIMERO â€” deben FALLAR antes de implementar) âš ï¸

- [X] T040 [P] [US3] Crear prueba unitaria `src/test/java/org/ups/liberacionesobra/application/usecase/DeteccionDuplicadosUseCaseTest.java` â€” **Dado** que `SolicitudInspeccionRepository.existePendientePara` retorna `true` para la terna proyecto+frente+actividad (stub Mockito), **Cuando** se invoca `CrearSolicitudInspeccionUseCase` con `forzar=false`, **Entonces** lanza `SolicitudDuplicadaException`; verificar tambiÃ©n que cuando `forzar=true` el caso de uso continÃºa sin lanzar excepciÃ³n (usa Mockito para `SolicitudInspeccionRepository` y `SolicitudDomainService`)
- [X] T041 [P] [US3] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/ValidacionCamposObligatoriosFuncionalTest.java` (@SpringBootTest + MockMvc) â€” **Dado** una peticiÃ³n POST sin `frenteId`, **Cuando** llega al controlador, **Entonces** la respuesta es HTTP 400 con `errores[0].campo == "frenteId"` y un mensaje descriptivo en espaÃ±ol; verificar el mismo comportamiento para `proyectoId` y `actividadId` ausentes
- [X] T042 [P] [US3] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/DuplicadoSolicitudFuncionalTest.java` (@SpringBootTest + MockMvc) con dos escenarios: (a) **Dado** solicitud activa PENDIENTE, **Cuando** POST sin `forzar`, **Entonces** HTTP 409 con `codigoError: "SOLICITUD_DUPLICADA"` y `solicitudExistenteId` presente; (b) **Dado** solicitud activa PENDIENTE, **Cuando** POST con `forzar: true`, **Entonces** HTTP 201 con nuevo `id`

### ImplementaciÃ³n de Historia de Usuario 3

- [X] T043 [US3] Crear `src/main/java/org/ups/liberacionesobra/domain/exception/SolicitudDuplicadaException.java` â€” excepciÃ³n de negocio sin anotaciones de framework; incluye `solicitudExistenteId` (UUID)
- [X] T044 [US3] La detecciÃ³n de duplicados ya estÃ¡ en `CrearSolicitudInspeccionUseCase` (paso 2 de T024). En esta tarea: aÃ±adir el parÃ¡metro `boolean forzar` a la firma del caso de uso y al objeto de comando; verificar que si `forzar=true` el caso de uso omite la comprobaciÃ³n de duplicados y continÃºa directamente al paso 3 (construcciÃ³n + persistencia) en `src/main/java/org/ups/liberacionesobra/application/usecase/CrearSolicitudInspeccionUseCase.java`
- [X] T045 [US3] Completar `src/main/java/org/ups/liberacionesobra/presentation/exception/GlobalExceptionHandler.java`: mapear `MethodArgumentNotValidException` â†’ HTTP 400 con `ErrorValidacionResponse` (lista de errores por campo con mensajes en espaÃ±ol); mapear `SolicitudDuplicadaException` â†’ HTTP 409 con `SolicitudDuplicadaResponse` (codigoError="SOLICITUD_DUPLICADA" + solicitudExistenteId)
- [X] T046 [US3] Validar Escenarios 3, 4 y 5 del `quickstart.md` ejecutando los comandos curl documentados y verificando los cÃ³digos HTTP y cuerpos de respuesta esperados

**Punto de control**: Las tres historias de usuario funcionan de forma independiente y correcta.

---

## Fase Final: Pulido y Aspectos Transversales

**PropÃ³sito**: Endpoints de catÃ¡logo con arquitectura correcta, validaciÃ³n de cobertura y Javadoc.

- [X] T047 [P] Implementar `src/main/java/org/ups/liberacionesobra/application/usecase/ConsultarCatalogosUseCase.java` â€” mÃ©todos: `listarProyectos()` (retorna `List<Proyecto>`), `listarFrentesPorProyecto(UUID proyectoId)` (retorna `List<Frente>`), `listarActividadesPorFrente(UUID frenteId)` (retorna `List<Actividad>`); delega en `ProyectoRepository`, `FrenteRepository`, `ActividadRepository` respectivamente; anotado con `@Service`
- [X] T048 [P] Implementar `src/main/java/org/ups/liberacionesobra/presentation/mapper/CatalogoDtoMapper.java` â€” convierte `Proyecto` â†’ `ProyectoResponse`, `Frente` â†’ `FrenteResponse`, `Actividad` â†’ `ActividadResponse` (DTOs generados por OpenAPI)
- [X] T049 Implementar `src/main/java/org/ups/liberacionesobra/presentation/controller/CatalogoController.java` â€” implementa la interfaz generada `CatalogoApi`; mÃ©todos: `listarProyectos`, `listarFrentesPorProyecto`, `listarActividadesPorFrente`; delega EXCLUSIVAMENTE en `ConsultarCatalogosUseCase` (nunca en repositorios de infraestructura directamente); usa `CatalogoDtoMapper`
- [X] T050 [P] Crear prueba funcional `src/test/java/org/ups/liberacionesobra/presentation/CatalogoControllerFuncionalTest.java` (@SpringBootTest + MockMvc) â€” **Dado** datos de catÃ¡logo precargados en data.sql, **Cuando** se hace GET a `/proyectos`, **Entonces** HTTP 200 con lista de proyectos; verificar mismo comportamiento para `/proyectos/{id}/frentes` y `/frentes/{id}/actividades`
- [X] T051 Ejecutar `./gradlew test jacocoTestReport jacocoTestCoverageVerification` â€” verificar que todas las pruebas pasan (verde), cobertura global >= 80% de instrucciones y ramas, cobertura por clase > 80%; ajustar exclusiones de JaCoCo en `build.gradle` si clases generadas o de configuraciÃ³n distorsionan el cÃ³mputo
- [X] T052 [P] Revisar y completar Javadoc en todas las interfaces pÃºblicas de `domain/repository/` y `application/usecase/` â€” describir intenciÃ³n y restricciones (no detalles de implementaciÃ³n)

---

## Dependencias y Orden de EjecuciÃ³n

### Dependencias entre Fases

- **ConfiguraciÃ³n (Fase 1)**: Sin dependencias â€” puede comenzar de inmediato
- **Fundamentos (Fase 2)**: Depende de T001â€“T004 completos â€” BLOQUEA todas las historias
- **HU-1 (Fase 3)**: Depende de Fase 2 completa â€” no depende de HU-2 ni HU-3
- **HU-2 (Fase 4)**: Depende de Fase 2; para los tests necesita HU-1 implementada (requiere solicitudes que mostrar en el listado)
- **HU-3 (Fase 5)**: Depende de Fase 2; para los tests de duplicados necesita HU-1 implementada
- **Final (Fase 6)**: Depende de que todas las historias requeridas estÃ©n completas

### Dependencias entre Historias de Usuario

- **HU-1 (P1)**: Sin dependencias de otras historias â€” puede comenzar tras los Fundamentos
- **HU-2 (P2)**: Depende de Fundamentos; para los tests integrados depende de HU-1
- **HU-3 (P3)**: Depende de Fundamentos; para los tests de duplicados depende de HU-1

### Dentro de Cada Historia de Usuario

- Pruebas BDD â†’ escritas y confirmadas fallidas ANTES de la implementaciÃ³n
- Entidades de dominio â†’ antes que casos de uso
- Casos de uso â†’ antes que controladores
- Infraestructura JPA â†’ antes que presentaciÃ³n
- Historia completa â†’ antes de pasar a la siguiente prioridad

### Oportunidades de Paralelismo

- T003 y T004 (Fase 1) pueden ejecutarse en paralelo
- T007, T008, T009, T011â€“T017 (Fase 2) pueden ejecutarse en paralelo entre sÃ­ (dentro del lÃ­mite de T010 completo)
- T019, T020, T021, T022 (pruebas HU-1) pueden escribirse en paralelo
- T023, T026, T027 (implementaciÃ³n HU-1) pueden ejecutarse en paralelo
- T047, T048, T050, T052 (Fase Final) pueden ejecutarse en paralelo entre sÃ­

---

## Ejemplo de Paralelismo: Historia de Usuario 1

```text
# 1. Escribir todas las pruebas de HU-1 juntas (en paralelo):
T019: CrearSolicitudInspeccionUseCaseTest.java
T020: SolicitudInspeccionJpaRepositoryTest.java
T021: CrearSolicitudInspeccionFuncionalTest.java
T022: ObtenerSolicitudInspeccionFuncionalTest.java

# 2. Confirmar que TODAS fallan antes de continuar

# 3. Implementar en paralelo donde es posible:
T023: SolicitudDomainService.java         (dominio â€” independiente)
T026: SolicitudInspeccionJpaEntity.java   (infraestructura â€” independiente)
T026: SolicitudNotFoundException.java     (dominio/exception â€” independiente)

# 4. Luego en paralelo:
T024: CrearSolicitudInspeccionUseCase.java  (depende de T023)
T025: ObtenerSolicitudInspeccionUseCase.java (depende de T026 SolicitudNotFoundException)
T027: SolicitudInspeccionJpaRepository.java  (depende de T026 JpaEntity)

# 5. Luego secuencialmente:
T028: SolicitudInspeccionRepositoryImpl.java (depende de T027 + T030 SolicitudMapper)
T030: SolicitudMapper.java                   (depende de T026 JpaEntity)
T031: SolicitudInspeccionController.java     (depende de T024 + T025 + T032 DtoMapper)
T032: SolicitudDtoMapper.java                (depende de T024)
T033: GlobalExceptionHandler (404)           (depende de T026 SolicitudNotFoundException)
```

---

## Estrategia de ImplementaciÃ³n

### MVP Primero (Solo Historia de Usuario 1)

1. Completar Fase 1: ConfiguraciÃ³n
2. Completar Fase 2: Fundamentos (CRÃTICO â€” bloquea todo)
3. Completar Fase 3: Historia de Usuario 1
4. **PARAR Y VALIDAR**: Escenario 1 del quickstart.md funciona end-to-end
5. Demostrar / desplegar MVP si estÃ¡ listo

### Entrega Incremental

1. ConfiguraciÃ³n + Fundamentos â†’ Base lista
2. AÃ±adir HU-1 â†’ Probar de forma independiente â†’ Desplegar/Demo (Â¡MVP!)
3. AÃ±adir HU-2 â†’ Probar de forma independiente â†’ Desplegar/Demo
4. AÃ±adir HU-3 â†’ Probar de forma independiente â†’ Desplegar/Demo
5. Fase Final â†’ CatÃ¡logos + cobertura + Javadoc

---

## Notas

- **[P]** = archivos distintos, sin dependencias entre sÃ­
- **[USn]** = traza la tarea a la historia de usuario para trazabilidad
- Las clases del paquete `presentation.generated` son generadas por OpenAPI y **NO deben editarse manualmente**
- `CatalogoController` **SIEMPRE** delega en `ConsultarCatalogosUseCase`, nunca directamente en repositorios de infraestructura (Principio I â€” Arquitectura Limpia)
- `@Data` de Lombok estÃ¡ **prohibido** en entidades de dominio; usar `@Getter` + `@Builder` + equals/hashCode manual por id
- Confirmar que las pruebas fallan ANTES de implementar (TDD/BDD â€” Principio II)
- Las excepciones de negocio (`SolicitudNotFoundException`, `SolicitudDuplicadaException`) viven en `domain/exception/` y no tienen anotaciones de Spring
- `SolicitudDomainService` es un servicio de construcciÃ³n de entidad puro: **nunca** inyecta repositorios; la detecciÃ³n de duplicados y la validaciÃ³n de catÃ¡logos son responsabilidad de `CrearSolicitudInspeccionUseCase` (capa de aplicaciÃ³n)

