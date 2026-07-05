<!--
Informe de Impacto de Sincronización
=====================================
Cambio de versión: 1.0.0 → 1.0.1
Principios modificados: Ninguno (cambio de idioma inglés → español; contenido equivalente)
Secciones añadidas: Ninguna
Secciones eliminadas: Ninguna
Plantillas que requieren actualización:
  - ✅ .specify/templates/plan-template.md — sin cambios estructurales requeridos
  - ✅ .specify/templates/spec-template.md — sin cambios estructurales requeridos
  - ✅ .specify/templates/tasks-template.md — sin cambios estructurales requeridos
TODOs pendientes: Ninguno
-->

# Constitución del liberacionesobra-service

## Principios Fundamentales

### I. Arquitectura Limpia (Robert C. Martin)

El sistema DEBE estructurarse siguiendo las capas de Arquitectura Limpia con inversión estricta
de dependencias:

- **Capa de Dominio**: Contiene entidades, objetos de valor, servicios de dominio e interfaces de
  repositorio. No tiene dependencias de frameworks ni sistemas externos. Solo lógica de negocio pura.
- **Capa de Aplicación**: Contiene casos de uso y servicios de aplicación que orquestan la lógica
  de dominio. Depende únicamente de la capa de dominio. NO DEBE referenciar directamente la
  infraestructura ni la presentación.
- **Capa de Infraestructura**: Contiene implementaciones concretas de repositorios (JPA, clientes
  HTTP), configuración y adaptadores externos. Depende únicamente de las capas de dominio y
  aplicación.
- **Capa de Presentación**: Contiene controladores REST, DTOs y mapeadores de solicitud/respuesta.
  Las interfaces de controlador y las clases modelo DEBEN generarse desde los contratos OpenAPI.
  Depende únicamente de la capa de aplicación.

**Regla de Dependencia**: Las dependencias del código fuente DEBEN apuntar únicamente hacia adentro
(hacia el dominio). Ninguna capa exterior puede ser referenciada por una capa interior.
Las anotaciones de frameworks NO DEBEN filtrarse hacia las capas de dominio o aplicación.

**Justificación**: Protege el dominio de cambios en los frameworks, habilita la testabilidad
independiente de las reglas de negocio y garantiza que el sistema sea adaptable a cambios de
infraestructura sin reescribir la lógica.

### II. Estrategia de Pruebas BDD

Todas las funcionalidades DEBEN estar cubiertas por tres niveles de pruebas usando Desarrollo
Guiado por Comportamiento (BDD):

- **Pruebas Unitarias**: Prueban clases individuales de las capas de dominio y aplicación de forma
  aislada. DEBEN seguir la estructura Dado-Cuando-Entonces (Given-When-Then). Los mocks están
  permitidos únicamente para colaboradores externos a la unidad bajo prueba.
  Framework: JUnit 5 + Mockito.
- **Pruebas de Integración**: Verifican que los componentes funcionan en conjunto (p. ej.,
  repositorio + JPA + H2). DEBEN usar slices reales del contexto Spring. La base de datos H2 en
  memoria es aceptable para este nivel.
  Framework: Spring Boot Test (`@DataJpaTest`, `@SpringBootTest`) + JUnit 5.
- **Pruebas Funcionales/Aceptación**: Validan escenarios de extremo a extremo a través de la API
  REST. Los escenarios DEBEN derivarse directamente de los criterios de aceptación de la
  especificación de funcionalidad y DEBEN usar nombres con estructura Dado-Cuando-Entonces.
  Framework: Spring Boot Test (MockMvc) + JUnit 5.

Las pruebas DEBEN escribirse ANTES de la implementación (TDD). Cada prueba DEBE confirmarse
fallida antes de que se provea la implementación correspondiente. Los nombres de los métodos de
prueba BDD DEBEN reflejar la cláusula Dado-Cuando-Entonces que validan.

**Justificación**: BDD garantiza que las pruebas documenten la intención y validen el comportamiento
del negocio, no los detalles de implementación. Escribir las pruebas primero detecta problemas de
diseño de forma temprana y garantiza que la cobertura sea deliberada y trazable a los requisitos.

### III. Principios de Ingeniería de Software (SOLID, YAGNI, DRY)

Todo el código producido en este proyecto DEBE cumplir con los siguientes principios:

**SOLID**:
- **S — Responsabilidad Única**: Cada clase DEBE tener exactamente una razón para cambiar.
- **O — Abierto/Cerrado**: Las clases DEBEN estar abiertas para extensión y cerradas para
  modificación.
- **L — Sustitución de Liskov**: Los subtipos DEBEN ser sustituibles por sus tipos base sin
  alterar la corrección del programa.
- **I — Segregación de Interfaces**: Las interfaces DEBEN ser específicas a las necesidades del
  cliente. Las interfaces gordas que agrupan operaciones no relacionadas están prohibidas.
- **D — Inversión de Dependencias**: Los módulos de alto nivel DEBEN depender de abstracciones,
  no de concreciones. Todas las dependencias entre capas DEBEN fluir a través de interfaces
  definidas en la capa interior.

**YAGNI (You Aren't Gonna Need It — No lo vas a necesitar)**: NO SE DEBE implementar
funcionalidades, abstracciones o infraestructura que no sean requeridas por una historia de usuario
concreta y aprobada en el momento presente. La generalización especulativa está prohibida.

**DRY (Don't Repeat Yourself — No te repitas)**: Todo fragmento de conocimiento DEBE tener una
única representación autoritativa en el sistema. La lógica duplicada DEBE extraerse en un
componente compartido con nombre propio.

**Justificación**: Estos principios reducen el acoplamiento, aumentan la cohesión y producen
sistemas mantenibles y extensibles sin introducir complejidad innecesaria ni código muerto.

### IV. API First

Todas las APIs REST DEBEN ser diseñadas y documentadas con un contrato OpenAPI 3.x ANTES de que
comience cualquier implementación. El contrato es la única fuente de verdad de la superficie de API.

- Los archivos de contrato OpenAPI DEBEN residir en `src/main/resources/openapi/` y versionarse
  en el control de código fuente junto con el código que describen.
- Las interfaces de controlador del lado servidor y las clases DTO/modelo cubiertas por el contrato
  DEBEN generarse usando `openapi-generator-gradle-plugin`. La creación manual de estos artefactos
  está prohibida para evitar divergencia entre contrato e implementación.
- Los cambios al contrato DEBEN ser revisados y aprobados antes de que comience la implementación.
- El versionado de APIs DEBE seguir versionado semántico expresado en el campo `info.version`
  de cada contrato OpenAPI. Los cambios que rompen compatibilidad DEBEN incrementar la versión mayor.
- El directorio de código generado DEBE excluirse del control de código fuente (añadido a
  `.gitignore`) y regenerarse como parte del proceso de build.

**Justificación**: API First garantiza que todos los consumidores y productores acuerden el contrato
de interfaz antes de que exista código. Previene la divergencia de documentación, habilita el
desarrollo paralelo de consumidores y productores, y hace que los cambios de ruptura sean
explícitos y revisables.

### V. Métricas de Calidad y Cobertura (JaCoCo)

La calidad del código DEBE validarse en cada build usando el plugin JaCoCo de Gradle:

- **Cobertura por clase**: Cada clase (excluyendo código generado y producido por Lombok) DEBE
  alcanzar > 80% de cobertura de líneas y ramas.
- **Cobertura global**: El proyecto en su totalidad DEBE alcanzar >= 80% de cobertura de líneas
  Y >= 80% de cobertura de ramas en todas las clases no excluidas.
- **Reportes**: JaCoCo DEBE generar reportes HTML y XML en cada ejecución de pruebas.
  Los reportes DEBEN escribirse en `build/reports/jacoco/test/`.
- **Puerta de calidad en el build**: El build de Gradle DEBE incluir una regla
  `jacocoTestCoverageVerification` que falle el build cuando la cobertura global caiga por debajo
  del 80%. Los builds que fallen esta puerta NO DEBEN mergearse a la rama principal.
- **Exclusiones**: Las clases autogeneradas por `openapi-generator` y las clases producidas por
  Lombok PUEDEN excluirse de la verificación de JaCoCo. Las exclusiones DEBEN listarse
  explícitamente en `build.gradle` con un comentario que justifique cada patrón de exclusión.

**Justificación**: Aplicar umbrales de cobertura como puerta de calidad en el build garantiza que
la estrategia de pruebas BDD definida en el Principio II se aplique de forma consistente y previene
que la degradación de calidad pase desapercibida.

## Estándares de Calidad de Código

- Todo el código de producción DEBE usar Java 25 y el framework Spring Boot 4.1.x.
- Las anotaciones de Lombok están permitidas para reducir el código repetitivo (constructores,
  getters, builders). Las entidades de dominio NO DEBEN usar `@Data`; los métodos equals y
  hashCode DEBEN basarse en la identidad de dominio.
- Todas las interfaces públicas y firmas de casos de uso DEBEN incluir Javadoc que describa la
  intención y las restricciones, no la implementación.
- La lógica de negocio NO DEBE residir en controladores, entidades JPA ni clases de configuración
  de Spring.
- Todas las excepciones no manejadas DEBEN mapearse a respuestas de error HTTP apropiadas en un
  manejador centralizado con `@ControllerAdvice`.
- El logging DEBE usar SLF4J. Niveles: DEBUG para estado interno, INFO para eventos de negocio,
  WARN para problemas recuperables, ERROR para fallos que requieren intervención.

## Flujo de Trabajo y Puertas de Calidad

1. **Puerta de contrato**: La funcionalidad DEBE tener un contrato OpenAPI aprobado y revisado
   antes de que comience la planificación o implementación.
2. **Puerta de especificación**: La funcionalidad DEBE tener una especificación aprobada con
   escenarios de aceptación BDD antes de crear un plan.
3. **Puerta de constitución**: La Verificación de Constitución en el plan DEBE pasar antes de que
   comience la investigación de la Fase 0, y DEBE verificarse nuevamente tras el diseño de la
   Fase 1.
4. **Puerta de pruebas**: Las pruebas DEBEN escribirse y confirmarse fallidas antes de que comience
   la implementación.
5. **Puerta de cobertura**: La verificación de cobertura JaCoCo DEBE superar los umbrales (>80%
   por clase, >=80% global) antes de que un PR pueda mergearse.
6. **Puerta de revisión**: Todos los PRs DEBEN revisarse verificando el cumplimiento de la
   Arquitectura Limpia y la adherencia a los principios SOLID, YAGNI y DRY.

Los nombres de los escenarios BDD en las pruebas DEBEN ser trazables a los criterios de aceptación
del documento de especificación. Cada historia de usuario DEBE tener al menos una prueba funcional
que cubra su escenario principal de camino feliz.

## Gobernanza

Esta constitución reemplaza todas las demás convenciones de código, guías de estilo o acuerdos
informales en este proyecto. Cualquier desviación de estos principios DEBE documentarse como una
excepción justificada con un plan de migración acordado antes de que el código desviante se
incorpore a la rama principal.

**Procedimiento de enmienda**:
1. Proponer la enmienda con justificación y evaluación de impacto.
2. Obtener aprobación explícita del equipo antes de implementar.
3. Actualizar este archivo de constitución con una versión incrementada siguiendo la política de
   versionado.
4. Actualizar todas las plantillas y documentos de guía dependientes en el mismo commit.
5. Referenciar la versión de la enmienda en la descripción del PR.

**Revisión de cumplimiento**: Las puertas de Verificación de Constitución en `plan.md` DEBEN
verificarse al crear el plan y reverificarse tras el diseño de la Fase 1. Los revisores DEBEN
rechazar PRs que violen estos principios sin justificación documentada.

**Política de versionado**:
- MAYOR: Eliminación o redefinición incompatible con versiones anteriores de un principio central.
- MENOR: Nuevo principio o sección añadida, o guía materialmente ampliada.
- PARCHE: Aclaración, mejora de redacción o refinamiento no semántico.

**Versión**: 1.0.1 | **Ratificada**: 2026-06-27 | **Última Enmienda**: 2026-06-27
