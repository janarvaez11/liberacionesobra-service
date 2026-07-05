# liberacionesobra-service

Microservicio para la gestión de solicitudes de inspección en el flujo de liberaciones de obra.
Permite crear solicitudes vinculadas a proyecto, frente y actividad; validar campos obligatorios; detectar duplicados activos; consultar catálogos; listar solicitudes por frente; y notificar automáticamente al residente mediante SSE cuando se crea una nueva solicitud.

El servicio fue desarrollado con **Java + Spring Boot**, aplicando **Spec-Driven Development (SDD)** con **Spec-Kit**, arquitectura por capas/puertos y adaptadores, contrato OpenAPI y validación automática mediante **Quality Agent**.

## Funcionalidad implementada

Historia principal:

> Como inspector de calidad, quiero crear una solicitud de inspección vinculada a proyecto, frente y actividad, para que el punto quede registrado en el sistema desde que producción lo solicita y no se pierda en mensajes informales.

La funcionalidad cubre los requisitos `RF-001` a `RF-006` definidos en `specs/001-crear-solicitud-inspeccion/spec.md`.

## Stack técnico

* Java 25, según toolchain configurado en Gradle
* Spring Boot 4.1.0
* Gradle Wrapper
* Spring Web
* Spring Data JPA
* H2 Database en memoria
* OpenAPI Generator
* JaCoCo
* JUnit 5 / Spring Boot Test
* SSE con `SseEmitter`

## Artefactos SDD / Spec-Kit

Los artefactos generados por Spec-Kit se encuentran en:

```text
specs/001-crear-solicitud-inspeccion/
```

Archivos principales:

| Archivo                  | Propósito                                             |
| ------------------------ | ----------------------------------------------------- |
| `spec.md`                | Especificación funcional y requisitos RF-001 a RF-006 |
| `plan.md`                | Plan técnico de implementación                        |
| `tasks.md`               | Tareas derivadas del plan                             |
| `data-model.md`          | Modelo de datos                                       |
| `research.md`            | Decisiones e investigación técnica                    |
| `quickstart.md`          | Guía rápida de uso                                    |
| `contracts/openapi.yaml` | Contrato OpenAPI de la funcionalidad                  |

## Arquitectura del proyecto

```text
src/main/java/org/ups/liberacionesobra/
├── domain/          # Modelo de dominio, excepciones, puertos y servicios puros
├── application/     # Casos de uso
├── infrastructure/  # Persistencia, mappers, repositorios y notificador SSE
└── presentation/    # Controladores REST, eventos SSE, DTOs y manejo de errores
```

Componentes principales:

| Componente                        | Responsabilidad                                                                 |
| --------------------------------- | ------------------------------------------------------------------------------- |
| `CrearSolicitudInspeccionUseCase` | Crea solicitudes, valida catálogos, detecta duplicados y dispara notificaciones |
| `SolicitudDomainService`          | Construye la solicitud con estado inicial `PENDIENTE` y fecha de creación       |
| `SseSolicitudNotificador`         | Implementa push automático mediante SSE                                         |
| `SolicitudEventosController`      | Expone el endpoint SSE para residentes                                          |
| `SolicitudInspeccionController`   | Expone endpoints REST de solicitudes                                            |
| `CatalogoController`              | Expone catálogos de proyectos, frentes y actividades                            |

## Base de datos

El servicio usa H2 en memoria para ejecución local y pruebas.

Configuración principal:

```text
src/main/resources/application.yaml
```

Datos iniciales:

```text
src/main/resources/data.sql
```

Consola H2:

```text
http://localhost:8080/h2-console
```

Configuración H2:

```text
JDBC URL: jdbc:h2:mem:liberaciones
User: sa
Password: vacío
```

## Ejecución local

En Windows:

```powershell
.\gradlew.bat bootRun
```

En Linux/Mac:

```bash
./gradlew bootRun
```

El servicio inicia en:

```text
http://localhost:8080
```

## API

Contrato OpenAPI:

```text
src/main/resources/openapi/liberacionesobra-api.yaml
```

Endpoints principales:

| Método | Endpoint                                             | Descripción                                      |
| ------ | ---------------------------------------------------- | ------------------------------------------------ |
| `POST` | `/solicitudes-inspeccion`                            | Crear solicitud de inspección                    |
| `GET`  | `/solicitudes-inspeccion`                            | Listar solicitudes con filtros                   |
| `GET`  | `/solicitudes-inspeccion/{id}`                       | Consultar una solicitud por ID                   |
| `GET`  | `/proyectos`                                         | Listar proyectos                                 |
| `GET`  | `/proyectos/{proyectoId}/frentes`                    | Listar frentes por proyecto                      |
| `GET`  | `/frentes/{frenteId}/actividades`                    | Listar actividades por frente                    |
| `GET`  | `/frentes/{frenteId}/solicitudes-inspeccion/eventos` | Suscripción SSE para actualizaciones automáticas |

## Actualización automática con SSE

El requisito `RF-005` exige que el residente vea las solicitudes de su frente actualizadas automáticamente, sin recargar manualmente la pantalla.

Para cumplirlo se implementó un mecanismo push con Server-Sent Events:

```text
GET /frentes/{frenteId}/solicitudes-inspeccion/eventos
```

Flujo:

1. El residente abre una conexión SSE para su frente.
2. El sistema registra un `SseEmitter` asociado al `frenteId`.
3. El inspector crea una nueva solicitud mediante `POST /solicitudes-inspeccion`.
4. `CrearSolicitudInspeccionUseCase` persiste la solicitud.
5. El caso de uso invoca `notificador.notificar(persistida)`.
6. `SseSolicitudNotificador` envía el evento `nueva-solicitud` a los residentes suscritos a ese frente.

Pruebas relevantes:

```text
SseSolicitudNotificadorTest
SolicitudEventosFuncionalTest
CrearSolicitudInspeccionUseCaseTest
```

## Pruebas

Ejecutar todas las pruebas:

```powershell
.\gradlew.bat clean test
```

Ejecutar pruebas con reporte de cobertura:

```powershell
.\gradlew.bat clean test jacocoTestReport
```

Ejecutar validación completa de cobertura:

```powershell
.\gradlew.bat clean test jacocoTestReport jacocoTestCoverageVerification
```

El reporte HTML de JaCoCo se genera en:

```text
build/reports/jacoco/test/html/index.html
```

Umbrales configurados:

```text
INSTRUCTION >= 80%
BRANCH >= 80%
```

## Quality Gate

La validación final fue realizada con el **Quality Agent** usando:

```text
/quality:verify C:\Pruebas\ProjectsIntelligentIdea\liberacionesobra-service
```

Resultado final:

| Pilar     | Resultado                      |
| --------- | ------------------------------ |
| Pruebas   | 62/62 pasan                    |
| Cobertura | 98.23%                         |
| Seguridad | 0 críticas, 0 high, 0 secretos |
| Criterios | 6/6 cumplen                    |
| Veredicto | APROBADO                       |

Archivos generados por el Quality Agent:

```text
quality-output/verification.json
quality-output/report.html
```

El reporte visual se encuentra en:

```text
quality-output/report.html
```

## Seguridad

El escaneo de seguridad se realizó con Semgrep CLI como evidencia sustituta, debido a que el MCP configurado no expuso herramientas invocables en la sesión del agente.

Resultado:

```text
0 vulnerabilidades críticas
0 hallazgos high
0 secretos expuestos
```

Nota: la autenticación/autorización queda fuera del alcance de esta historia según los supuestos del `spec.md`.

## Comandos útiles

```powershell
# Ejecutar servicio
.\gradlew.bat bootRun

# Ejecutar pruebas
.\gradlew.bat clean test

# Ejecutar pruebas + cobertura
.\gradlew.bat clean test jacocoTestReport

# Validar cobertura
.\gradlew.bat jacocoTestCoverageVerification

# Ejecutar build completo
.\gradlew.bat clean build
```

## Estado final

El servicio queda aprobado por el Quality Agent porque cumple la Definition of Done:

* La funcionalidad fue implementada a partir de `spec.md`.
* Los artefactos de Spec-Kit están versionados.
* Todos los requisitos funcionales tienen evidencia.
* Las pruebas pasan.
* La cobertura supera el umbral.
* Seguridad no presenta hallazgos bloqueantes.
* El reporte de calidad fue generado correctamente.
